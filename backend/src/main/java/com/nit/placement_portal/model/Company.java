package com.nit.placement_portal.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "companies")
public class Company {
    
    @Id
    private String id;

    private String name;
    private String logoUrl;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    @JsonProperty("logo")
    public String getLogo() {
        return logoUrl;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @JsonAlias("logo")
    public void setLogo(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
