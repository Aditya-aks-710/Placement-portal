package com.nit.placement_portal.dto;

import java.util.List;

public class PublicStudentFilterOptionsDTO {

    private List<String> companies;
    private List<String> branches;
    private List<String> batches;

    public List<String> getCompanies() {
        return companies;
    }

    public void setCompanies(List<String> companies) {
        this.companies = companies;
    }

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }

    public List<String> getBatches() {
        return batches;
    }

    public void setBatches(List<String> batches) {
        this.batches = batches;
    }
}
