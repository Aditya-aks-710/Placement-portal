package com.nit.placement_portal.repository;

import com.nit.placement_portal.model.Education;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends MongoRepository<Education, String> {
    List<Education> findByStudentId(String studentId);
}