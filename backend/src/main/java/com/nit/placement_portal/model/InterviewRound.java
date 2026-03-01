package com.nit.placement_portal.model;

import java.util.*;

public class InterviewRound {

    private String roundType;

    private String month;

    private String year;

    private List<InterviewQuestion> questions;

    public String getRoundType() {
        return roundType;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public List<InterviewQuestion> getQuestions() {
        return questions;
    }

    public void setRoundType(String roundType) {
        this.roundType = roundType;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setQuestions(List<InterviewQuestion> questions) {
        this.questions = questions;
    }
}
