package com.nit.placement_portal.controller;

import com.nit.placement_portal.dto.PublicStudentDTO;
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

    @GetMapping(params = "status")
    public List<PublicStudentDTO> getStudentsByStatus(@RequestParam String status) {
        return studentService.getStudentsByStatus(status);
    }
}
