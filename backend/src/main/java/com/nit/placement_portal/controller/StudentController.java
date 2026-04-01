package com.nit.placement_portal.controller;

import com.nit.placement_portal.dto.PublicStudentDTO;
import com.nit.placement_portal.dto.PublicStudentFilterOptionsDTO;
import com.nit.placement_portal.dto.PublicStudentPageDTO;
import com.nit.placement_portal.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/students")
public class StudentController {
    
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<PublicStudentDTO> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/page")
    public PublicStudentPageDTO getStudentsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String batch
    ) {
        return studentService.getStudentsPage(page, size, search, status, company, branch, batch);
    }

    @GetMapping("/filters")
    public PublicStudentFilterOptionsDTO getStudentFilterOptions() {
        return studentService.getStudentFilterOptions();
    }

    @GetMapping(params = "status")
    public List<PublicStudentDTO> getStudentsByStatus(@RequestParam String status) {
        return studentService.getStudentsByStatus(status);
    }
}
