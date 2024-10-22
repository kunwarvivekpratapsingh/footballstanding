package com.example.footballmatch.service;

import com.example.footballmatch.exceptionalhandling.ResourceNotFoundException;
import com.example.footballmatch.model.Country;
import com.example.footballmatch.model.FinalStandingResponse;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface FootballServiceInterface
{
    FinalStandingResponse getTeamStandings(String countryName, String leagueName, String teamName) throws JsonProcessingException, ResourceNotFoundException;
    Country getCountryIdByName(String countryName) throws JsonProcessingException, ResourceNotFoundException;
}