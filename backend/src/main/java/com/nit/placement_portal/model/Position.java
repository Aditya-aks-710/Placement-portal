package com.nit.placement_portal.model;

/**
 * A single role/position a student held at a company. A company record can hold
 * several positions over time (e.g. "IT Trainee" internship that converts into a
 * "Developer" full-time role), forming a LinkedIn-style timeline.
 */
public class Position {

    private String id;
    private String title;
    private String type; // "internship" or "full-time"
    private String startDate;
    private String endDate; // null/blank = "Present"
    private String stipend; // for internship positions
    private String ctc; // for full-time positions

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStipend() {
        return stipend;
    }

    public void setStipend(String stipend) {
        this.stipend = stipend;
    }

    public String getCtc() {
        return ctc;
    }

    public void setCtc(String ctc) {
        this.ctc = ctc;
    }
}
