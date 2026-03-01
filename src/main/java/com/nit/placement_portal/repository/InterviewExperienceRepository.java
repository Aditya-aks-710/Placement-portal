package com.nit.placement_portal.repository;

import com.nit.placement_portal.model.InterviewExperience;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface InterviewExperienceRepository extends MongoRepository<InterviewExperience, String> {
    
    List<InterviewExperience> findByPlacementId(String placementId);

    List<InterviewExperience> findByStudentId(String studentId);
}
