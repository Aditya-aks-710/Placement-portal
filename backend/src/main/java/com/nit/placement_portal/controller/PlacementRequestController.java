package com.nit.placement_portal.controller;

import com.nit.placement_portal.dto.PlacementRequestDTO;
import com.nit.placement_portal.exception.ResourceNotFoundException;
import com.nit.placement_portal.exception.UnauthorizedException;
import com.nit.placement_portal.model.PlacementRequest;
import com.nit.placement_portal.model.User;
import com.nit.placement_portal.repository.PlacementRequestRepository;
import com.nit.placement_portal.repository.UserRepository;
import com.nit.placement_portal.service.StudentService;
import com.nit.placement_portal.service.CompanyService;
import com.nit.placement_portal.model.Company;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/placement-requests")
public class PlacementRequestController {

    private final PlacementRequestRepository placementRequestRepository;
    private final StudentService studentService;
    private final CompanyService companyService;
    private final UserRepository userRepository;

    public PlacementRequestController(
            PlacementRequestRepository placementRequestRepository,
            StudentService studentService,
            CompanyService companyService,
            UserRepository userRepository) {
        this.placementRequestRepository = placementRequestRepository;
        this.studentService = studentService;
        this.companyService = companyService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public PlacementRequest submitRequest(@RequestBody PlacementRequestDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String studentId = user.getStudentId();

        Company company = resolveCompany(dto);

        PlacementRequest request = new PlacementRequest();
        request.setStudentId(studentId);
        request.setCompanyId(company.getId());
        request.setCompany(company.getName());
        request.setCompanyLogo(company.getLogoUrl());
        request.setRole(dto.getRole());
        request.setCtc(dto.getCtc());
        request.setPlacementYear(dto.getPlacementYear());
        request.setCampusMode(dto.getCampusMode());
        request.setPlacementNature(dto.getPlacementNature());
        request.setStatus("PENDING");

        placementRequestRepository.save(request);

        studentService.markStudentPending(studentId);

        return request;
    }

    private Company resolveCompany(PlacementRequestDTO dto) {
        String companyId = dto.getCompanyId();
        if (companyId != null && !companyId.isBlank()) {
            return companyService.getCompanyById(companyId);
        }

        String companyName = dto.getCompanyName();
        if (companyName != null && !companyName.isBlank()) {
            return companyService.createCompany(companyName.trim(), null);
        }

        throw new ResourceNotFoundException("Company is required");
    }
}