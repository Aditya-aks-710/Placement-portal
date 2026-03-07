package com.nit.placement_portal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyDTO {
    private String id;
    private String name;
    private String role;
    private String packageValue;
    private String internshipStipend;
    private String fullTimePackage;
    private String joinDate;
    private String endDate;
    private String type;
    private String duration;
    private Boolean converted;
    private String conversionType;
    private String conversionDate;
    private String logo;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @JsonProperty("package")
    public String getPackage() {
        return packageValue;
    }

    @JsonProperty("package")
    public void setPackage(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getInternshipStipend() {
        return internshipStipend;
    }

    @JsonAlias("stipend")
    public void setInternshipStipend(String internshipStipend) {
        this.internshipStipend = internshipStipend;
    }

    @JsonProperty("stipend")
    public String getStipend() {
        return internshipStipend;
    }

    @JsonProperty("stipend")
    public void setStipend(String stipend) {
        this.internshipStipend = stipend;
    }

    public String getFullTimePackage() {
        return fullTimePackage;
    }

    public void setFullTimePackage(String fullTimePackage) {
        this.fullTimePackage = fullTimePackage;
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

    public String getLogo() {
        return logo;
    }

    @JsonAlias("logoUrl")
    public void setLogo(String logo) {
        this.logo = logo;
    }
}
