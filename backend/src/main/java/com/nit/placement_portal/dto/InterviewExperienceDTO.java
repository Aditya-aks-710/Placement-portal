package com.nit.placement_portal.dto;

import java.util.List;

public class InterviewExperienceDTO {

    private String studentId;
    
    private String placementId;

    private String company;

    private List<PublicInterviewRoundDTO> rounds;

    private String overallTips;

    private String difficulty;

    private Integer rating;

    public String getStudentId() {
        return studentId;
    }

    public String getPlacementId() {
        return placementId;
    }

    public String getCompany() {
        return company;
    }

    public List<PublicInterviewRoundDTO> getRounds() {
        return rounds;
    }

    public String getOverallTips() {
        return overallTips;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Integer getRating() {
        return rating;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setRounds(List<PublicInterviewRoundDTO> rounds) {
        this.rounds = rounds;
    }

    public void setOverallTips(String overallTips) {
        this.overallTips = overallTips;
    }
}
