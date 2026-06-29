package com.nit.placement_portal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nit.placement_portal.exception.BadRequestException;
import com.nit.placement_portal.exception.ResourceNotFoundException;
import com.nit.placement_portal.model.PlacementRequest;
import com.nit.placement_portal.model.Student;
import com.nit.placement_portal.model.StudentPlacement;
import com.nit.placement_portal.repository.PlacementRequestRepository;
import com.nit.placement_portal.repository.StudentPlacementRepository;
import com.nit.placement_portal.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class PlacementServiceTest {

    @Mock
    private PlacementRequestRepository placementRequestRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentPlacementRepository studentPlacementRepository;

    @InjectMocks
    private PlacementService placementService;

    private PlacementRequest pendingRequest;
    private Student student;

    @BeforeEach
    void setUp() {
        pendingRequest = new PlacementRequest();
        pendingRequest.setId("req-1");
        pendingRequest.setStudentId("stud-1");
        pendingRequest.setCompanyId("comp-1");
        pendingRequest.setCompany("Google");
        pendingRequest.setCompanyLogo("logo.png");
        pendingRequest.setRole("SDE");
        pendingRequest.setCtc(30);
        pendingRequest.setPlacementYear(2025);
        pendingRequest.setStatus("PENDING");

        student = new Student();
        student.setId("stud-1");
    }

    @Test
    void approveRequest_marksStudentPlacedAndSavesHistory() {
        when(placementRequestRepository.findById("req-1")).thenReturn(Optional.of(pendingRequest));
        when(studentRepository.findById("stud-1")).thenReturn(Optional.of(student));

        PlacementRequest result = placementService.approveRequest("req-1");

        assertEquals("APPROVED", result.getStatus());
        assertEquals("PLACED", student.getStatus());
        assertEquals("Google", student.getCompany());

        ArgumentCaptor<StudentPlacement> historyCaptor = ArgumentCaptor.forClass(StudentPlacement.class);
        verify(studentPlacementRepository).save(historyCaptor.capture());
        assertEquals("Google", historyCaptor.getValue().getCompany());
        assertEquals("stud-1", historyCaptor.getValue().getStudentId());
    }

    @Test
    void approveRequest_throwsWhenNotPending() {
        pendingRequest.setStatus("APPROVED");
        when(placementRequestRepository.findById("req-1")).thenReturn(Optional.of(pendingRequest));

        assertThrows(BadRequestException.class, () -> placementService.approveRequest("req-1"));
        verify(studentPlacementRepository, never()).save(any());
    }

    @Test
    void approveRequest_throwsWhenRequestMissing() {
        when(placementRequestRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> placementService.approveRequest("missing"));
    }

    @Test
    void rejectRequest_marksStudentUnplaced() {
        when(placementRequestRepository.findById("req-1")).thenReturn(Optional.of(pendingRequest));
        when(studentRepository.findById("stud-1")).thenReturn(Optional.of(student));

        PlacementRequest result = placementService.rejectRequest("req-1");

        assertEquals("REJECTED", result.getStatus());
        assertEquals("UNPLACED", student.getStatus());
        verify(studentPlacementRepository, never()).save(any());
    }

    @Test
    void rejectRequest_throwsWhenNotPending() {
        pendingRequest.setStatus("REJECTED");
        when(placementRequestRepository.findById("req-1")).thenReturn(Optional.of(pendingRequest));

        assertThrows(BadRequestException.class, () -> placementService.rejectRequest("req-1"));
    }
}
