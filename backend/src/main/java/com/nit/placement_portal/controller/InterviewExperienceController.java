package com.nit.placement_portal.controller;

import org.springframework.web.bind.annotation.*;

import com.nit.placement_portal.dto.InterviewExperienceDTO;
import com.nit.placement_portal.dto.PublicInterviewExperienceDTO;
import com.nit.placement_portal.dto.PublicInterviewRoundDTO;
import com.nit.placement_portal.exception.ResourceNotFoundException;
import com.nit.placement_portal.exception.UnauthorizedException;
import com.nit.placement_portal.model.InterviewExperience;
import com.nit.placement_portal.model.InterviewQuestion;
import com.nit.placement_portal.model.InterviewRound;
import com.nit.placement_portal.model.User;
import com.nit.placement_portal.repository.InterviewExperienceRepository;
import com.nit.placement_portal.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
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
    public PublicInterviewExperienceDTO addExperience(
            @RequestBody InterviewExperienceDTO dto) {

        Authentication auth =
                SecurityContextHolder.getContext()
                        .getAuthentication();
        
        if(auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        String username = auth.getName();

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        // Determine whose profile this experience belongs to. Admins may post to any
        // student's profile; students may only post to their own.
        String targetStudentId = dto.getStudentId();
        if (targetStudentId == null || targetStudentId.isBlank()) {
            targetStudentId = user.getStudentId();
        } else if (!isAdmin && !targetStudentId.equals(user.getStudentId())) {
            throw new UnauthorizedException("You can only add experiences to your own profile");
        }

        if (targetStudentId == null || targetStudentId.isBlank()) {
            throw new UnauthorizedException("No student profile is associated with this account");
        }

        InterviewExperience experience = new InterviewExperience();
        experience.setStudentId(targetStudentId);
        experience.setPlacementId(dto.getPlacementId());
        experience.setCompanyName(dto.getCompany());
        experience.setRounds(toRounds(dto.getRounds()));
        experience.setOverallTips(dto.getOverallTips());
        experience.setDifficulty(dto.getDifficulty());
        experience.setRating(dto.getRating() == null ? null : dto.getRating().floatValue());
        experience.setPlacedHere(dto.getPlacedHere());

        InterviewExperience saved = interviewExperienceRepository.save(experience);
        return toPublicDTO(saved);
    }

    @GetMapping("/placement/{placementId}")
    public List<PublicInterviewExperienceDTO> getExperiencesByPlacementId(@PathVariable String placementId) {
        return interviewExperienceRepository.findAll()
                .stream()
                .filter(experience -> placementId.equals(experience.getPlacementId()))
                .map(this::toPublicDTO)
                .toList();
    }

    @GetMapping("/my")
    public List<PublicInterviewExperienceDTO> getMyExperiences() {
        Authentication auth =
                SecurityContextHolder.getContext()
                        .getAuthentication();
        
        if(auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        String username = auth.getName();

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return interviewExperienceRepository.findByStudentId(user.getStudentId())
                .stream()
                .map(this::toPublicDTO)
                .toList();
    }

    private List<InterviewRound> toRounds(List<PublicInterviewRoundDTO> roundDTOs) {
        if (roundDTOs == null) {
            return List.of();
        }

        List<InterviewRound> rounds = new ArrayList<>();
        for (PublicInterviewRoundDTO roundDTO : roundDTOs) {
            InterviewRound round = new InterviewRound();
            // Persist mock-format fields into existing model slots for backward compatibility.
            round.setRoundType(roundDTO.getName());
            round.setMonth(roundDTO.getDescription());
            round.setYear(roundDTO.getTips());
            round.setQuestions(List.of());
            rounds.add(round);
        }
        return rounds;
    }

    private PublicInterviewExperienceDTO toPublicDTO(InterviewExperience experience) {
        PublicInterviewExperienceDTO dto = new PublicInterviewExperienceDTO();
        dto.setCompany(experience.getCompanyName());
        dto.setOverallTips(experience.getOverallTips());
        dto.setDifficulty(experience.getDifficulty());
        dto.setRating(experience.getRating() == null ? null : Math.round(experience.getRating()));
        dto.setPlacedHere(experience.getPlacedHere());

        List<PublicInterviewRoundDTO> rounds = new ArrayList<>();
        if (experience.getRounds() != null) {
            for (InterviewRound round : experience.getRounds()) {
                PublicInterviewRoundDTO roundDTO = new PublicInterviewRoundDTO();
                roundDTO.setName(round.getRoundType());
                roundDTO.setDescription(round.getMonth());
                roundDTO.setTips(firstNonBlank(round.getYear(), resolveTipFromQuestions(round.getQuestions())));
                rounds.add(roundDTO);
            }
        }
        dto.setRounds(rounds);
        return dto;
    }

    private String resolveTipFromQuestions(List<InterviewQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            return "Prepare fundamentals and communicate clearly.";
        }
        InterviewQuestion first = questions.get(0);
        if (first.getQuestion() != null && !first.getQuestion().isBlank()) {
            return first.getQuestion();
        }
        if (first.getLink() != null && !first.getLink().isBlank()) {
            return "Practice from: " + first.getLink();
        }
        return "Prepare fundamentals and communicate clearly.";
    }

    private String firstNonBlank(String first, String fallback) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return fallback;
    }
}
