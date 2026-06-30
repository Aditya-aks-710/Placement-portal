package com.nit.placement_portal.controller;

import com.nit.placement_portal.dto.CompanyDTO;
import com.nit.placement_portal.dto.PositionDTO;
import com.nit.placement_portal.exception.BadRequestException;
import com.nit.placement_portal.exception.ResourceNotFoundException;
import com.nit.placement_portal.exception.UnauthorizedException;
import com.nit.placement_portal.model.Company;
import com.nit.placement_portal.model.Position;
import com.nit.placement_portal.model.Student;
import com.nit.placement_portal.model.StudentCompany;
import com.nit.placement_portal.model.User;
import com.nit.placement_portal.repository.StudentRepository;
import com.nit.placement_portal.repository.UserRepository;
import com.nit.placement_portal.service.CompanyService;
import com.nit.placement_portal.service.StudentCompanyService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
public class StudentCompanyController {

    private final StudentRepository studentRepository;
    private final CompanyService companyService;
    private final StudentCompanyService studentCompanyService;
    private final UserRepository userRepository;

    public StudentCompanyController(
            StudentRepository studentRepository,
            CompanyService companyService,
            StudentCompanyService studentCompanyService,
            UserRepository userRepository
    ) {
        this.studentRepository = studentRepository;
        this.companyService = companyService;
        this.studentCompanyService = studentCompanyService;
        this.userRepository = userRepository;
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
        ensureOwnerOrAdmin(studentId);

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
        ensureOwnerOrAdmin(studentId);

        StudentCompany existing = studentCompanyService.getStudentCompanyById(studentCompanyId)
                .orElseThrow(() -> new ResourceNotFoundException("Student company record not found"));

        if (!studentId.equals(existing.getStudentId())) {
            throw new BadRequestException("Student company record does not belong to this student");
        }

        applyCompanyPayload(existing, dto, false);
        StudentCompany saved = studentCompanyService.saveStudentCompany(existing);
        syncStudentPlacementSnapshot(studentId);
        return toCompanyDTO(saved);
    }

    @DeleteMapping("/{studentId}/companies/{studentCompanyId}")
    public void deleteStudentCompany(
            @PathVariable String studentId,
            @PathVariable String studentCompanyId
    ) {
        ensureStudentExists(studentId);
        ensureOwnerOrAdmin(studentId);

        StudentCompany existing = studentCompanyService.getStudentCompanyById(studentCompanyId)
                .orElseThrow(() -> new ResourceNotFoundException("Student company record not found"));

        if (!studentId.equals(existing.getStudentId())) {
            throw new BadRequestException("Student company record does not belong to this student");
        }

        studentCompanyService.deleteStudentCompany(studentCompanyId);
        syncStudentPlacementSnapshot(studentId);
    }

    /** A logged-in student may only edit their own profile; admins may edit anyone. */
    private void ensureOwnerOrAdmin(String studentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            return;
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!studentId.equals(user.getStudentId())) {
            throw new UnauthorizedException("You can only edit your own profile");
        }
    }

    private void applyCompanyPayload(StudentCompany target, CompanyDTO dto, boolean isCreate) {
        String companyName = firstNonBlank(dto.getName());

        if (isCreate && companyName == null) {
            throw new BadRequestException("Company name is required");
        }

        if (companyName != null) {
            Company company = companyService.createCompany(companyName, firstNonBlank(dto.getLogo()));
            target.setCompanyId(company.getId());
        }

        if (target.getCompanyId() == null || target.getCompanyId().isBlank()) {
            throw new BadRequestException("Company is required");
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

        // When the client sends an explicit role timeline, persist it and recompute
        // the flat fields from it so status/list rendering stays consistent.
        if (dto.getPositions() != null) {
            applyPositions(target, dto.getPositions());
        }
    }

    /** Persist the position timeline and derive the flat company fields from it. */
    private void applyPositions(StudentCompany target, List<PositionDTO> positionDtos) {
        List<Position> positions = new ArrayList<>();
        for (PositionDTO pd : positionDtos) {
            if (isBlank(pd.getTitle())) {
                continue;
            }
            Position p = pd.toModel();
            if (isBlank(p.getId())) {
                p.setId(UUID.randomUUID().toString());
            }
            positions.add(p);
        }

        if (positions.isEmpty()) {
            throw new BadRequestException("At least one position with a title is required");
        }

        target.setPositions(positions);

        Position firstPosition = positions.get(0);
        Position lastPosition = positions.get(positions.size() - 1);

        Position lastInternship = null;
        Position firstFullTime = null;
        Position lastFullTime = null;
        for (Position p : positions) {
            if ("internship".equalsIgnoreCase(p.getType())) {
                lastInternship = p;
            } else {
                if (firstFullTime == null) {
                    firstFullTime = p;
                }
                lastFullTime = p;
            }
        }

        boolean hasInternship = lastInternship != null;
        boolean hasFullTime = lastFullTime != null;
        boolean converted = hasInternship && hasFullTime;

        target.setRole(lastPosition.getTitle());
        target.setType(firstNonBlank(lastPosition.getType(), "full-time"));
        target.setInternshipStipend(lastInternship != null ? lastInternship.getStipend() : null);
        target.setFullTimePackage(lastFullTime != null ? lastFullTime.getCtc() : null);
        target.setJoinDate(firstPosition.getStartDate());
        target.setEndDate(lastPosition.getEndDate());
        target.setConverted(converted);
        if (converted && firstFullTime != null) {
            target.setConversionDate(firstNonBlank(firstFullTime.getStartDate(), target.getConversionDate()));
            if (isBlank(target.getConversionType())) {
                target.setConversionType("PPO");
            }
        }
        target.setPackageValue(chooseEffectivePackage(
                target.getType(),
                target.getInternshipStipend(),
                target.getFullTimePackage()));
    }

    private void syncStudentPlacementSnapshot(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<StudentCompany> studentCompanies = studentCompanyService.getStudentCompanies(studentId);
        StudentCompany active = pickActiveCompany(studentCompanies);
        if (active == null) {
            // No company records left (or none currently active): reset the student
            // back to an unplaced state and clear the cached company fields.
            if (studentCompanies.isEmpty()) {
                student.setCompany(null);
                student.setCompanyLogo(null);
                student.setStatus("UNPLACED");
                studentRepository.save(student);
            }
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
        dto.setPositions(PositionDTO.deriveFrom(studentCompany));

        String effectivePackage = firstNonBlank(
                studentCompany.getPackageValue(),
                chooseEffectivePackage(studentCompany.getType(), studentCompany.getInternshipStipend(), studentCompany.getFullTimePackage())
        );
        dto.setPackageValue(effectivePackage);
        return dto;
    }

    private void ensureStudentExists(String studentId) {
        if (studentRepository.findById(studentId).isEmpty()) {
            throw new ResourceNotFoundException("Student not found");
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
