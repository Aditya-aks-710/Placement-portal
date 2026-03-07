package com.nit.placement_portal.service;

import com.nit.placement_portal.model.Skill;
import com.nit.placement_portal.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> getStudentSkills(String studentId) {
        return skillRepository.findByStudentId(studentId);
    }

    public Skill saveSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public void deleteSkill(String id) {
        skillRepository.deleteById(id);
    }
}