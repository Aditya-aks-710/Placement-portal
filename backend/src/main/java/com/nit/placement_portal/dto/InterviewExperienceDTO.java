package com.nit.placement_portal.dto;

import com.nit.placement_portal.model.InterviewRound;
import java.util.List;


public class InterviewExperienceDTO {
    
    private String placementId;

    private String company;

    private List<InterviewRound> rounds;

    private String overallTips;

    private String difficulty;

    private Float rating;

    public String getPlacementId() {
        return placementId;
    }

    public String getCompany() {
        return company;
    }

    public List<InterviewRound> getRounds() {
        return rounds;
    }

    public String getOverallTips() {
        return overallTips;
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

    public Float getRating() {
        return rating;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public void setRounds(List<InterviewRound> rounds) {
        this.rounds = rounds;
    }

    public void setOverallTips(String overallTips) {
        this.overallTips = overallTips;
    }
}
