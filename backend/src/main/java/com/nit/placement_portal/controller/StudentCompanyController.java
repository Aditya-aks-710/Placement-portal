package com.nit.placement_portal.controller;

import com.nit.placement_portal.dto.CompanyDTO;
import com.nit.placement_portal.model.Company;
import com.nit.placement_portal.model.Student;
import com.nit.placement_portal.model.StudentCompany;
import com.nit.placement_portal.repository.StudentRepository;
import com.nit.placement_portal.service.CompanyService;
import com.nit.placement_portal.service.StudentCompanyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentCompanyController {

    private final StudentRepository studentRepository;
    private final CompanyService companyService;
    private final StudentCompanyService studentCompanyService;

    public StudentCompanyController(
            StudentRepository studentRepository,
            CompanyService companyService,
            StudentCompanyService studentCompanyService
    ) {
        this.studentRepository = studentRepository;
        this.companyService = companyService;
        this.studentCompanyService = studentCompanyService;
    }

    @GetMapping("/{studentId}/companies")
    public List<CompanyDTO> getStudentCompanies(@PathVariable String studentId) {
        return studentCompanyService.getStudentCompanies(studentId).stream()
                .map(this::toCompanyDTO)
                .toList();
    }

    @PostMapping("/{studentId}/companies")
    public CompanyDTO addStudentCompany(@PathVariable String studentId, @RequestBody CompanyDTO dto) {
        ensureStudentExists(studentId);

        StudentCompany studentCompany = new StudentCompany();
        studentCompany.setStudentId(studentId);
        applyCompanyPayload(studentCompany, dto, true);

        StudentCompany saved = studentCompanyService.saveStudentCompany(studentCompany);
        syncStudentPlacementSnapshot(studentId);
        return toCompanyDTO(saved);
    }

    @PutMapping("/{studentId}/companies/{studentCompanyId}")
    public CompanyDTO updateStudentCompany(
            @PathVariable String studentId,
            @PathVariable String studentCompanyId,
            @RequestBody CompanyDTO dto
    ) {
        ensureStudentExists(studentId);

        StudentCompany existing = studentCompanyService.getStudentCompanyById(studentCompanyId)
                .orElseThrow(() -> new RuntimeException("Student company record not found"));

        if (!studentId.equals(existing.getStudentId())) {
            throw new RuntimeException("Student company record does not belong to this student");
        }

        applyCompanyPayload(existing, dto, false);
        StudentCompany saved = studentCompanyService.saveStudentCompany(existing);
        syncStudentPlacementSnapshot(studentId);
        return toCompanyDTO(saved);
    }

    private void applyCompanyPayload(StudentCompany target, CompanyDTO dto, boolean isCreate) {
        String companyName = firstNonBlank(dto.getName());

        if (isCreate && companyName == null) {
            throw new RuntimeException("Company name is required");
        }

        if (companyName != null) {
            Company company = companyService.createCompany(companyName, firstNonBlank(dto.getLogo()));
            target.setCompanyId(company.getId());
        }

        if (target.getCompanyId() == null || target.getCompanyId().isBlank()) {
            throw new RuntimeException("Company is required");
        }

        String type = firstNonBlank(dto.getType(), target.getType(), "full-time");
        String basePackage = firstNonBlank(dto.getPackageValue(), dto.getPackage(), target.getPackageValue());
        String stipend = firstNonBlank(dto.getInternshipStipend(), dto.getStipend(), target.getInternshipStipend());
        String fullTimePackage = firstNonBlank(dto.getFullTimePackage(), target.getFullTimePackage());

        if ("internship".equalsIgnoreCase(type) && stipend == null && basePackage != null) {
            stipend = basePackage;
        }

        if ("full-time".equalsIgnoreCase(type) && fullTimePackage == null && basePackage != null) {
            fullTimePackage = basePackage;
        }

        Boolean convertedFlag = firstNonNull(
                dto.getConverted(),
                target.getConverted(),
                (fullTimePackage != null && !fullTimePackage.isBlank())
                        || (dto.getConversionType() != null && !dto.getConversionType().isBlank())
                        || (dto.getConversionDate() != null && !dto.getConversionDate().isBlank())
        );

        target.setRole(firstNonBlank(dto.getRole(), target.getRole()));
        target.setType(type);
        target.setPackageValue(firstNonBlank(basePackage, chooseEffectivePackage(type, stipend, fullTimePackage)));
        target.setInternshipStipend(stipend);
        target.setFullTimePackage(fullTimePackage);
        target.setJoinDate(firstNonBlank(dto.getJoinDate(), target.getJoinDate()));
        target.setEndDate(firstNonBlank(dto.getEndDate(), target.getEndDate()));
        target.setDuration(firstNonBlank(dto.getDuration(), target.getDuration()));
        target.setConverted(convertedFlag);
        target.setConversionType(firstNonBlank(dto.getConversionType(), target.getConversionType()));
        target.setConversionDate(firstNonBlank(dto.getConversionDate(), target.getConversionDate()));
    }

    private void syncStudentPlacementSnapshot(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<StudentCompany> studentCompanies = studentCompanyService.getStudentCompanies(studentId);
        StudentCompany active = pickActiveCompany(studentCompanies);
        if (active == null) {
            return;
        }

        Company company = companyService.getCompanyById(active.getCompanyId());
        student.setCompany(company.getName());
        student.setCompanyLogo(company.getLogoUrl());

        if (isConverted(active) || "full-time".equalsIgnoreCase(active.getType())) {
            student.setStatus("PLACED");
        } else if ("internship".equalsIgnoreCase(active.getType())) {
            student.setStatus("INTERNSHIP");
        }

        studentRepository.save(student);
    }

    private StudentCompany pickActiveCompany(List<StudentCompany> companies) {
        for (StudentCompany company : companies) {
            if (isConverted(company)) {
                return company;
            }
        }

        for (StudentCompany company : companies) {
            if (isBlank(company.getEndDate()) && "full-time".equalsIgnoreCase(company.getType())) {
                return company;
            }
        }

        for (StudentCompany company : companies) {
            if (isBlank(company.getEndDate()) && "internship".equalsIgnoreCase(company.getType())) {
                return company;
            }
        }

        return null;
    }

    private boolean isConverted(StudentCompany company) {
        return Boolean.TRUE.equals(company.getConverted())
                || (company.getFullTimePackage() != null && !company.getFullTimePackage().isBlank());
    }

    private CompanyDTO toCompanyDTO(StudentCompany studentCompany) {
        Company company = companyService.getCompanyById(studentCompany.getCompanyId());

        CompanyDTO dto = new CompanyDTO();
        dto.setId(studentCompany.getId());
        dto.setName(company.getName());
        dto.setLogo(company.getLogoUrl());
        dto.setRole(studentCompany.getRole());
        dto.setInternshipStipend(studentCompany.getInternshipStipend());
        dto.setFullTimePackage(studentCompany.getFullTimePackage());
        dto.setConverted(studentCompany.getConverted());
        dto.setConversionType(studentCompany.getConversionType());
        dto.setConversionDate(studentCompany.getConversionDate());
        dto.setJoinDate(studentCompany.getJoinDate());
        dto.setEndDate(studentCompany.getEndDate());
        dto.setType(studentCompany.getType());
        dto.setDuration(studentCompany.getDuration());

        String effectivePackage = firstNonBlank(
                studentCompany.getPackageValue(),
                chooseEffectivePackage(studentCompany.getType(), studentCompany.getInternshipStipend(), studentCompany.getFullTimePackage())
        );
        dto.setPackageValue(effectivePackage);
        return dto;
    }

    private void ensureStudentExists(String studentId) {
        if (studentRepository.findById(studentId).isEmpty()) {
            throw new RuntimeException("Student not found");
        }
    }

    private String chooseEffectivePackage(String type, String stipend, String fullTimePackage) {
        if ("internship".equalsIgnoreCase(type)) {
            return firstNonBlank(stipend, fullTimePackage);
        }
        return firstNonBlank(fullTimePackage, stipend);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    @SafeVarargs
    private final <T> T firstNonNull(T... values) {
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
