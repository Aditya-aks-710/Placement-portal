package com.nit.placement_portal.dto;

import com.nit.placement_portal.model.InterviewRound;
import java.util.List;


public class InterviewExperienceDTO {
    
    private String placementId;

    private String companyName;

    private List<InterviewRound> rounds;

    private String overallTips;

    public String getPlacementId() {
        return placementId;
    }

    public String getCompanyName() {
        return companyName;
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

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setRounds(List<InterviewRound> rounds) {
        this.rounds = rounds;
    }

    public void setOverallTips(String overallTips) {
        this.overallTips = overallTips;
    }
}
