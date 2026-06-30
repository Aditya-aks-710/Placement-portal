package com.nit.placement_portal.dto;

public class PlacementRequestDTO {

    private String studentId;
    private String companyId;
    private String companyName;
    private String role;
    private double ctc;
    private double stipend;
    private int placementYear;
    private String startMonth;
    private String campusMode;
    private String placementNature;
    private String engagementType;
    private String requestType;
    private String targetCompanyRecordId;


    public String getStudentId() {
        return studentId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRole() {
        return role;
    }

    public double getCtc() {
        return ctc;
    }

    public int getPlacementYear() {
        return placementYear;
    }

    public String getCampusMode() {
        return campusMode;
    }

    public String getPlacementNature() {
        return placementNature;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCtc(double ctc) {
        this.ctc = ctc;
    }

    public void setPlacementYear(int placementYear) {
        this.placementYear = placementYear;
    }

    public void setCampusMode(String campusMode) {
        this.campusMode = campusMode;
    }

    public void setPlacementNature(String placementNature) {
        this.placementNature = placementNature;
    }

    public double getStipend() {
        return stipend;
    }

    public void setStipend(double stipend) {
        this.stipend = stipend;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getEngagementType() {
        return engagementType;
    }

    public void setEngagementType(String engagementType) {
        this.engagementType = engagementType;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getTargetCompanyRecordId() {
        return targetCompanyRecordId;
    }

    public void setTargetCompanyRecordId(String targetCompanyRecordId) {
        this.targetCompanyRecordId = targetCompanyRecordId;
    }
}
