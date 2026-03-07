package com.nit.placement_portal.controller;

import org.springframework.web.bind.annotation.*;

import com.nit.placement_portal.model.InterviewExperience;
import com.nit.placement_portal.model.User;
import com.nit.placement_portal.repository.InterviewExperienceRepository;
import com.nit.placement_portal.repository.UserRepository;
import com.nit.placement_portal.dto.InterviewExperienceDTO;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@RestController
@RequestMapping("/api/experiences")
public class InterviewExperienceController {
    
    private final InterviewExperienceRepository interviewExperienceRepository;
    private final UserRepository userRepository;
    
    public InterviewExperienceController(
        InterviewExperienceRepository interviewExperienceRepository,
        UserRepository userRepository) {

        this.interviewExperienceRepository = interviewExperienceRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public InterviewExperience addExperience(
            @RequestBody InterviewExperienceDTO dto) {

        Authentication auth =
                SecurityContextHolder.getContext()
                        .getAuthentication();
        
        if(auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        String username = auth.getName();

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(
                                () -> new RuntimeException("User not found"));

        InterviewExperience experience = new InterviewExperience();
        experience.setStudentId(user.getStudentId());
        experience.setPlacementId(dto.getPlacementId());
        experience.setCompanyName(dto.getCompany());
        experience.setRounds(dto.getRounds());
        experience.setOverallTips(dto.getOverallTips());
        experience.setDifficulty(dto.getDifficulty());
        experience.setRating(dto.getRating());

        return interviewExperienceRepository.save(experience);
    }

    @GetMapping("/placement/{placementId}")
    public List<InterviewExperience> getExperiencesByPlacementId(@PathVariable String placementId) {
        return interviewExperienceRepository.findAll()
                .stream()
                .filter(experience -> placementId.equals(experience.getPlacementId()))
                .toList();
    }

    @GetMapping("/my")
    public List<InterviewExperience> getMyExperiences() {
        Authentication auth =
                SecurityContextHolder.getContext()
                        .getAuthentication();
        
        if(auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        String username = auth.getName();

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow();

        return interviewExperienceRepository.findByStudentId(user.getStudentId());
    }
}
