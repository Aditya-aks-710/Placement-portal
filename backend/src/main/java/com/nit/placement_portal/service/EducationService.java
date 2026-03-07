package com.nit.placement_portal.service;

import com.nit.placement_portal.model.Education;
import com.nit.placement_portal.repository.EducationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationService {

    private final EducationRepository educationRepository;

    public EducationService(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    public List<Education> getStudentEducation(String studentId) {
        return educationRepository.findByStudentId(studentId);
    }

    public Education saveEducation(Education education) {
        return educationRepository.save(education);
    }

    public void deleteEducation(String id) {
        educationRepository.deleteById(id);
    }
}