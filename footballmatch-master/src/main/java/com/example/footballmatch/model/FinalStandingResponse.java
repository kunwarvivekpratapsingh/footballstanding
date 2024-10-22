package com.example.footballmatch.model;

import lombok.Data;

@Data
public class FinalStandingResponse
{
    private String countryId;
    private String countryName;
    private String leagueId;
    private String leagueName;
    private String teamId;
    private String teamName;
    private String overAllLeaguePosition;
}