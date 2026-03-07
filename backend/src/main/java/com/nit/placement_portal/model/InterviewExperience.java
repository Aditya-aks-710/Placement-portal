package com.nit.placement_portal.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.*;


@Document(collection = "interview_experiences")
public class InterviewExperience {
    
    @Id
    private String id;

    private String studentId;

    private String placementId;

    private String companyName;

    private List<InterviewRound> rounds;

    private String overallTips;

    private String difficulty;

    private Float rating;

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

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

    public void setId(String id) {
        this.id = id;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
}