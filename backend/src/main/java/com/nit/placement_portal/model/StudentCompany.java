package com.nit.placement_portal.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "student_companies")
public class StudentCompany {

    @Id
    private String id;

    private String studentId;
    private String companyId;
    private String role;
    private String packageValue;
    private String internshipStipend;
    private String fullTimePackage;
    private String joinDate;
    private String endDate;
    private String type; // "full-time" or "internship"
    private String duration;
    private Boolean converted;
    private String conversionType; // "PPO", "FTE", etc.
    private String conversionDate;

    // LinkedIn-style role timeline. When present this is the source of truth for
    // the roles held at this company; the flat fields above are kept in sync for
    // status/list rendering and backward compatibility.
    private List<Position> positions;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInternshipStipend() {
        return internshipStipend;
    }

    public void setInternshipStipend(String internshipStipend) {
        this.internshipStipend = internshipStipend;
    }

    public String getFullTimePackage() {
        return fullTimePackage;
    }

    public void setFullTimePackage(String fullTimePackage) {
        this.fullTimePackage = fullTimePackage;
    }

    public Boolean getConverted() {
        return converted;
    }

    public void setConverted(Boolean converted) {
        this.converted = converted;
    }

    public String getConversionType() {
        return conversionType;
    }

    public void setConversionType(String conversionType) {
        this.conversionType = conversionType;
    }

    public String getConversionDate() {
        return conversionDate;
    }

    public void setConversionDate(String conversionDate) {
        this.conversionDate = conversionDate;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
}