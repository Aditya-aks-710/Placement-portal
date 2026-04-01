package com.nit.placement_portal.repository;

import com.nit.placement_portal.model.Skill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends MongoRepository<Skill, String> {
    List<Skill> findByStudentId(String studentId);
    List<Skill> findByStudentIdIn(List<String> studentIds);
}
