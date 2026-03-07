package com.nit.placement_portal.service;

import com.nit.placement_portal.model.StudentCompany;
import com.nit.placement_portal.repository.StudentCompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentCompanyService {

    private final StudentCompanyRepository studentCompanyRepository;

    public StudentCompanyService(StudentCompanyRepository studentCompanyRepository) {
        this.studentCompanyRepository = studentCompanyRepository;
    }

    public List<StudentCompany> getStudentCompanies(String studentId) {
        return studentCompanyRepository.findByStudentId(studentId);
    }

    public StudentCompany saveStudentCompany(StudentCompany studentCompany) {
        return studentCompanyRepository.save(studentCompany);
    }

    public Optional<StudentCompany> getStudentCompanyById(String id) {
        return studentCompanyRepository.findById(id);
    }

    public void deleteStudentCompany(String id) {
        studentCompanyRepository.deleteById(id);
    }
}
