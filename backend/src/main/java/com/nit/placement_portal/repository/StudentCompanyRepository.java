package com.nit.placement_portal.repository;

import com.nit.placement_portal.model.StudentCompany;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCompanyRepository extends MongoRepository<StudentCompany, String> {
    List<StudentCompany> findByStudentId(String studentId);
    List<StudentCompany> findByCompanyId(String companyId);
    List<StudentCompany> findByStudentIdIn(List<String> studentIds);
}
