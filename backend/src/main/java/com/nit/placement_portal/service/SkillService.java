package com.nit.placement_portal.service;

import com.nit.placement_portal.model.Skill;
import com.nit.placement_portal.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    /**
     * Replaces the full set of skills for a student. Names are trimmed,
     * blanks are dropped, and duplicates (case-insensitive) are removed
     * while preserving order. Returns the persisted skills.
     */
    public List<Skill> replaceStudentSkills(String studentId, List<String> names) {
        List<Skill> existing = skillRepository.findByStudentId(studentId);
        if (!existing.isEmpty()) {
            skillRepository.deleteAll(existing);
        }

        List<Skill> toSave = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        if (names != null) {
            for (String name : names) {
                if (name == null) {
                    continue;
                }
                String trimmed = name.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if (!seen.add(trimmed.toLowerCase(Locale.ROOT))) {
                    continue;
                }
                Skill skill = new Skill();
                skill.setStudentId(studentId);
                skill.setName(trimmed);
                toSave.add(skill);
            }
        }

        if (toSave.isEmpty()) {
            return new ArrayList<>();
        }
        return skillRepository.saveAll(toSave);
    }
}