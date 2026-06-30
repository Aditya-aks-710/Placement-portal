package com.nit.placement_portal.service;

import com.nit.placement_portal.model.PlacementRequest;
import com.nit.placement_portal.model.Position;
import com.nit.placement_portal.model.Student;
import com.nit.placement_portal.model.StudentCompany;
import com.nit.placement_portal.model.StudentPlacement;
import com.nit.placement_portal.exception.BadRequestException;
import com.nit.placement_portal.exception.ResourceNotFoundException;
import com.nit.placement_portal.repository.PlacementRequestRepository;
import com.nit.placement_portal.repository.StudentCompanyRepository;
import com.nit.placement_portal.repository.StudentPlacementRepository;
import com.nit.placement_portal.repository.StudentRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class PlacementService {
    
    private final PlacementRequestRepository placementRequestRepository;
    private final StudentRepository studentRepository;
    private final StudentPlacementRepository studentPlacementRepository;
    private final StudentCompanyRepository studentCompanyRepository;

    public PlacementService(
        PlacementRequestRepository placementRequestRepository,
        StudentRepository studentRepository,
        StudentPlacementRepository studentPlacementRepository,
        StudentCompanyRepository studentCompanyRepository) {
        this.placementRequestRepository = placementRequestRepository;
        this.studentRepository = studentRepository;
        this.studentPlacementRepository = studentPlacementRepository;
        this.studentCompanyRepository = studentCompanyRepository;
    }

    public List<PlacementRequest> getRequestsByStatus(String status) {
        return placementRequestRepository.findByStatus(status);
    }

    public List<PlacementRequest> getRequestsByCampusMode(String campusMode) {
        return placementRequestRepository.findByCampusMode(campusMode);
    }

    public List<PlacementRequest> getRequestsByPlacementNature(String placementNature) {
        return placementRequestRepository.findByPlacementNature(placementNature);
    }

    public PlacementRequest approveRequest(String requestId) {

        PlacementRequest request = placementRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request Not Found"));

        if(!"PENDING".equals(request.getStatus())) {
            throw new BadRequestException("Only pending requests can be approved");
        }

        request.setStatus("APPROVED");
        placementRequestRepository.save(request);

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student Not Found"));

        if ("CONVERSION".equalsIgnoreCase(request.getRequestType())) {
            applyConversion(student, request);
        } else {
            applyPlacement(student, request);
        }

        // Keep a flat history snapshot for backward compatibility.
        StudentPlacement history = new StudentPlacement();
        history.setStudentId(request.getStudentId());
        history.setCompanyId(request.getCompanyId());
        history.setCompany(request.getCompany());
        history.setCompanyLogo(request.getCompanyLogo());
        history.setRole(request.getRole());
        history.setCtc(request.getCtc());
        history.setPlacementYear(request.getPlacementYear());
        history.setCampusMode(request.getCampusMode());
        history.setPlacementNature(request.getPlacementNature());
        studentPlacementRepository.save(history);

        return request;
    }

    private void applyPlacement(Student student, PlacementRequest request) {
        String engagement = normalizeEngagement(request.getEngagementType());
        boolean isInternship = engagement.startsWith("2M") || engagement.startsWith("6M");
        boolean converted = isInternship && (engagement.contains("PPO") || engagement.contains("FTE"));
        String conversionType = converted ? (engagement.contains("PPO") ? "PPO" : "FTE") : null;
        boolean fullTime = "FTE".equals(engagement);

        // Close any currently active records so the new one becomes the "current" position
        // and older ones move into the LinkedIn-style timeline.
        List<StudentCompany> existing = studentCompanyRepository.findByStudentId(student.getId());
        // The previous role ends exactly when the new one begins (month + year when available).
        String closeDate = buildJoinDate(request.getStartMonth(), request.getPlacementYear());
        for (StudentCompany sc : existing) {
            boolean changed = false;
            if (isBlank(sc.getEndDate())) {
                sc.setEndDate(closeDate);
                changed = true;
            }
            // Also close the last open-ended role in the timeline so it stops showing "Present".
            if (sc.getPositions() != null && !sc.getPositions().isEmpty()) {
                Position last = sc.getPositions().get(sc.getPositions().size() - 1);
                if (isBlank(last.getEndDate())) {
                    last.setEndDate(closeDate);
                    changed = true;
                }
            }
            if (changed) {
                studentCompanyRepository.save(sc);
            }
        }

        StudentCompany record = new StudentCompany();
        record.setStudentId(student.getId());
        record.setCompanyId(request.getCompanyId());
        record.setRole(request.getRole());
        record.setType(fullTime ? "full-time" : "internship");
        record.setJoinDate(buildJoinDate(request.getStartMonth(), request.getPlacementYear()));
        record.setDuration(durationFor(engagement));

        String stipend = formatStipend(request.getStipend());
        String ctc = formatCtc(request.getCtc());
        if (isInternship) {
            record.setInternshipStipend(stipend);
            record.setPackageValue(stipend);
        }
        if (fullTime) {
            record.setFullTimePackage(ctc);
            record.setPackageValue(ctc);
        }
        if (converted) {
            record.setConverted(true);
            record.setConversionType(conversionType);
            record.setConversionDate(closeDate);
            record.setFullTimePackage(ctc);
            record.setPackageValue(ctc);
        }
        studentCompanyRepository.save(record);

        student.setStatus(isInternship && !converted ? "INTERNSHIP" : "PLACED");
        student.setCompany(request.getCompany());
        student.setCompanyLogo(request.getCompanyLogo());
        studentRepository.save(student);
    }

    private void applyConversion(Student student, PlacementRequest request) {
        String engagement = normalizeEngagement(request.getEngagementType());
        String conversionType = engagement.contains("FTE") ? "FTE" : "PPO";

        StudentCompany target = null;
        if (!isBlank(request.getTargetCompanyRecordId())) {
            target = studentCompanyRepository.findById(request.getTargetCompanyRecordId()).orElse(null);
        }
        if (target == null) {
            // Fall back to the active internship record for this student.
            target = studentCompanyRepository.findByStudentId(student.getId()).stream()
                    .filter(sc -> isBlank(sc.getEndDate()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No internship found to convert"));
        }

        String ctc = formatCtc(request.getCtc());
        target.setConverted(true);
        target.setConversionType(conversionType);
        target.setConversionDate(String.valueOf(request.getPlacementYear()));
        target.setFullTimePackage(ctc);
        target.setPackageValue(ctc);
        if (!isBlank(request.getRole())) {
            target.setRole(request.getRole());
        }
        studentCompanyRepository.save(target);

        student.setStatus("PLACED");
        if (!isBlank(request.getCompany())) {
            student.setCompany(request.getCompany());
            student.setCompanyLogo(request.getCompanyLogo());
        }
        studentRepository.save(student);
    }

    private String normalizeEngagement(String engagementType) {
        if (engagementType == null) {
            return "FTE";
        }
        return engagementType.trim().toUpperCase(Locale.ROOT).replace(" ", "");
    }

    private String durationFor(String engagement) {
        if (engagement.startsWith("2M")) {
            return "2 months";
        }
        if (engagement.startsWith("6M")) {
            return "6 months";
        }
        return null;
    }

    private String buildJoinDate(String startMonth, int year) {
        if (startMonth != null && !startMonth.isBlank()) {
            return startMonth.trim() + " " + year;
        }
        return String.valueOf(year);
    }

    private String formatStipend(double stipend) {
        if (stipend <= 0) {
            return null;
        }
        if (stipend % 1 == 0) {
            return String.format(Locale.ROOT, "%.0fK/month", stipend);
        }
        return String.format(Locale.ROOT, "%.1fK/month", stipend);
    }

    private String formatCtc(double ctc) {
        if (ctc <= 0) {
            return null;
        }
        if (ctc % 1 == 0) {
            return String.format(Locale.ROOT, "%.0f LPA", ctc);
        }
        return String.format(Locale.ROOT, "%.1f LPA", ctc);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public PlacementRequest rejectRequest(String requestId) {
        PlacementRequest request = placementRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request Not Found"));
        
        if(!"PENDING".equals(request.getStatus())) {
            throw new BadRequestException("Only Pending Requests can be Rejected");
        }

        request.setStatus("REJECTED");
        placementRequestRepository.save(request);

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student Not Found"));

        // Only revert to UNPLACED if the student has no other approved company records.
        boolean hasOtherRecords = !studentCompanyRepository.findByStudentId(student.getId()).isEmpty();
        if (!hasOtherRecords) {
            student.setStatus("UNPLACED");
            studentRepository.save(student);
        }
        
        return request;
    }
}
