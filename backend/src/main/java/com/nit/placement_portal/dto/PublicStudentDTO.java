package com.nit.placement_portal.dto;

import java.util.List;

public class PublicStudentDTO {
    private String id;
    private String regNo;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String profilePic;
    private String branch;
    private String batch;
    private String status;

    private String company;
    private String companyLogo;
    private String role;
    private String packageValue;
    private String joinDate;
    private String endDate;
    private String type;
    private String duration;

    private CompanyDTO currentCompany;
    private List<CompanyDTO> pastCompanies;
    private List<PublicInterviewExperienceDTO> interviewExperiences;
    private List<EducationDTO> education;
    private List<String> skills;
    private String linkedin;
    private String github;
    private String bio;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
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

    public CompanyDTO getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(CompanyDTO currentCompany) {
        this.currentCompany = currentCompany;
    }

    public List<CompanyDTO> getPastCompanies() {
        return pastCompanies;
    }

    public void setPastCompanies(List<CompanyDTO> pastCompanies) {
        this.pastCompanies = pastCompanies;
    }

    public List<PublicInterviewExperienceDTO> getInterviewExperiences() {
        return interviewExperiences;
    }

    public void setInterviewExperiences(List<PublicInterviewExperienceDTO> interviewExperiences) {
        this.interviewExperiences = interviewExperiences;
    }

    public List<EducationDTO> getEducation() {
        return education;
    }

    public void setEducation(List<EducationDTO> education) {
        this.education = education;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
