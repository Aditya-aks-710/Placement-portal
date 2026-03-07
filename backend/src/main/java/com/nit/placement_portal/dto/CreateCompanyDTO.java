package com.nit.placement_portal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CreateCompanyDTO {
    
    private String name;

    @JsonAlias("logo")
    private String logoUrl;

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
