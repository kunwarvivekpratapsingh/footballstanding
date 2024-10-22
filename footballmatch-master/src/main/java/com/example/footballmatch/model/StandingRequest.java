package com.example.footballmatch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StandingRequest
{
    @JsonProperty("country")
    private String countryName;

    @JsonProperty("league")
    private String leagueName;

    @JsonProperty("team")
    private String teamName;
}