package com.nit.placement_portal.controller;

import com.nit.placement_portal.exception.ResourceNotFoundException;
import com.nit.placement_portal.exception.UnauthorizedException;
import com.nit.placement_portal.model.Skill;
import com.nit.placement_portal.model.User;
import com.nit.placement_portal.repository.StudentRepository;
import com.nit.placement_portal.repository.UserRepository;
import com.nit.placement_portal.service.SkillService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class SkillController {

    private final SkillService skillService;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public SkillController(
            SkillService skillService,
            StudentRepository studentRepository,
            UserRepository userRepository
    ) {
        this.skillService = skillService;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{studentId}/skills")
    public List<String> getStudentSkills(@PathVariable String studentId) {
        return skillService.getStudentSkills(studentId).stream()
                .map(Skill::getName)
                .filter(name -> name != null && !name.isBlank())
                .toList();
    }

    /** Replace a student's full skill list. Owner or admin only. */
    @PutMapping("/{studentId}/skills")
    public List<String> updateStudentSkills(
            @PathVariable String studentId,
            @RequestBody SkillsUpdateRequest request
    ) {
        ensureStudentExists(studentId);
        ensureOwnerOrAdmin(studentId);

        return skillService.replaceStudentSkills(studentId, request.getSkills()).stream()
                .map(Skill::getName)
                .filter(name -> name != null && !name.isBlank())
                .toList();
    }

    public static class SkillsUpdateRequest {
        private List<String> skills;

        public List<String> getSkills() {
            return skills;
        }

        public void setSkills(List<String> skills) {
            this.skills = skills;
        }
    }

    private void ensureStudentExists(String studentId) {
        if (studentRepository.findById(studentId).isEmpty()) {
            throw new ResourceNotFoundException("Student not found");
        }
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
}
