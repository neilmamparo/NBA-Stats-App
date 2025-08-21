package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("abbreviation")
    private String abbreviation;

    // Getters and setters

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return fullName + " (" + abbreviation + ")";
    }
}