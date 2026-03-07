package com.nit.placement_portal.dto;

import java.util.List;

public class PublicInterviewExperienceDTO {
    private String company;
    private List<PublicInterviewRoundDTO> rounds;
    private String overallTips;
    private String difficulty;
    private Integer rating;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<PublicInterviewRoundDTO> getRounds() {
        return rounds;
    }

    public void setRounds(List<PublicInterviewRoundDTO> rounds) {
        this.rounds = rounds;
    }

    public String getOverallTips() {
        return overallTips;
    }

    public void setOverallTips(String overallTips) {
        this.overallTips = overallTips;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
