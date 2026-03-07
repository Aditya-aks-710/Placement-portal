package com.nit.placement_portal.dto;

import java.util.List;

public class StudentDTO {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String branch;
    private String batch;
    private String status;
    private CompanyDTO currentCompany;
    private List<CompanyDTO> pastCompanies;
    private List<InterviewExperienceDTO> interviewExperiences;
    private List<EducationDTO> education;
    private List<String> skills;
    private String linkedin;
    private String github;
    private String bio;

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

    public List<InterviewExperienceDTO> getInterviewExperiences() {
        return interviewExperiences;
    }

    public void setInterviewExperiences(List<InterviewExperienceDTO> interviewExperiences) {
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