package com.example.footballmatch.service;

import com.example.footballmatch.exceptionalhandling.ResourceNotFoundException;
import com.example.footballmatch.model.*;
import com.example.footballmatch.utils.ProgramConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class FootballService implements FootballServiceInterface
{
    @Autowired
    RestTemplate restTemplate;

    private String footballServiceURL = ProgramConstants.BASE_URL;
    private String API_KEY = ProgramConstants.API_KEY;

    @Override
    public Country getCountryIdByName(String countryName) throws JsonProcessingException, ResourceNotFoundException
    {
        System.out.println("10. countryName: " + countryName );
        Country countryData = new Country();

        //Step-1: call countries API to get countryId by using countryName
        String getCountries_url = footballServiceURL + "?action=get_countries" + API_KEY;
        System.out.println("10.1 getCountries_url: " + getCountries_url );

        ResponseEntity<Country[]> countryResponse = restTemplate.getForEntity(getCountries_url, Country[].class);

        List<Country> countries = Arrays.stream(countryResponse.getBody()).filter(c -> c.getCountryName().equals(countryName)).collect(Collectors.toList());

        if(countries.stream().count() > 0)
        {
            countryData = countries.stream().findFirst().get();
        }

        System.out.println("10.3 countryData: " + countryData );

        return countryData;
    }

    @Override
    public FinalStandingResponse getTeamStandings(String countryName, String leagueName, String teamName) throws JsonProcessingException, ResourceNotFoundException
    {
        System.out.println("1.1 countryName: " + countryName + ", leagueName: " + leagueName + ", teamName: " + teamName);

        Country countryData = new Country();
        League leagueData = new League();
        Teams teamData = new Teams();
        Standings rankingData = new Standings();

        //call Country API to get league ID
        try
        {
            //////////////////////////////////////
            //Step-1: get countries //////////////
            String getCountries_url = footballServiceURL + "?action=get_countries" + API_KEY;
            System.out.println("1.2 getCountries_url: " + getCountries_url );

            ResponseEntity<Country[]> countryResponse = null;

            try
            {
                countryResponse = restTemplate.getForEntity(getCountries_url, Country[].class);
            }
            catch (RestClientException e)
            {
                throw new ResourceNotFoundException("No such Country name found. Please check Country name !!!");
            }

            List<Country> countries = Arrays.stream(countryResponse.getBody()).filter(c -> c.getCountryName().equals(countryName)).collect(Collectors.toList());

            if(countries.stream().count() > 0)
            {
                countryData = countries.stream().findFirst().get();
            }
            else
            {
                throw new ResourceNotFoundException("No such Country name found. Please check Country name !!!");
            }
            System.out.println("1.3 countryData: " + countryData );

            ////////////////////////////////////////
            //Step-2: get league name //////////////
            String get_leagues_url = footballServiceURL + "?action=get_leagues&country_id=" + countryData.getCountryId() + API_KEY;
            System.out.println("1.4 get_leagues_url: " + get_leagues_url );

            ResponseEntity<League[]> leagueResponse = null;
            try
            {
                leagueResponse = restTemplate.exchange(get_leagues_url, HttpMethod.GET, getObjectHttpEntity(), League[].class);
            }
            catch (RestClientException e)
            {
                throw new ResourceNotFoundException("No such (Country + Leauge) combination found. Please check Leauge in this combination!!");
            }

            List<League> leagues = Arrays.stream(leagueResponse.getBody()).filter(c -> c.getLeagueName().equals(leagueName)).collect(Collectors.toList());

            if(leagues.stream().count() > 0)
            {
                leagueData = leagues.stream().findFirst().get();
            }
            else
            {
                throw new ResourceNotFoundException("No such (Country + Leauge) combination found. Please check Leauge in this combination!!");
            }
            System.out.println("1.5 leagueData: " + leagueData );

            //////////////////////////////////////////////////////
            //Step-3: call teams API to get teamdata //////////////
            String get_teams_url = footballServiceURL + "?action=get_teams&league_id=" + leagueData.getLeagueId() + API_KEY;
            System.out.println("1.6 get_teams_url: " + get_teams_url );

            ResponseEntity<Teams[]> teamsResponse = null;
            try
            {
                teamsResponse = restTemplate.exchange(get_teams_url, HttpMethod.GET, getObjectHttpEntity(), Teams[].class);
            }
            catch (RestClientException e)
            {
                throw new ResourceNotFoundException("No such (Country + Leauge + Team) combination found. Please check Team name in this combination !!");
            }

            List<Teams> teams = Arrays.stream(teamsResponse.getBody()).filter(t -> t.getTeamName().equals(teamName)).collect(Collectors.toList());

            if(teams.stream().count() > 0)
            {
                teamData = teams.stream().findFirst().get();
            }
            else
            {
                throw new ResourceNotFoundException("No such (Country + Leauge + Team) combination found. Please check Team name in this combination !!");
            }
            System.out.println("1.7 teamData: " + teamData );

            //////////////////////////////////////////////////////
            //Step-4: Call standings API to get rankingData //////
            String get_standings_url = footballServiceURL + "?action=get_standings&league_id=" + leagueData.getLeagueId() + API_KEY;
            System.out.println("1.8 get_standings_url: " + get_standings_url );

            ResponseEntity<Standings[]> standingsResponse = null;
            try
            {
                standingsResponse = restTemplate.exchange(get_standings_url, HttpMethod.GET, getObjectHttpEntity(), Standings[].class);
            }
            catch (RestClientException e)
            {
                throw new ResourceNotFoundException("No such Standing for the combination of (Contry + League + Team) found. Please check your plan !!");
            }

            List<Standings> rankings = Arrays.stream(standingsResponse.getBody()).filter(s -> s.getTeamName().equals(teamName)).collect(Collectors.toList());

            if(rankings.stream().count() > 0)
            {
                rankingData = rankings.stream().findFirst().get();
            }
            else
            {
                throw new ResourceNotFoundException("No such Standing for the combination of (Contry + League + Team) found. Please check your plan !!");
            }
            System.out.println("1.9 rankingData: " + rankingData );

        }
        catch (NoSuchElementException e)
        {
            throw new ResourceNotFoundException("The Country Name/League Name or the team name are incorrect");
        }

        return findRankOfTeam(countryData, leagueData, teamData, rankingData);
    }

    private HttpEntity<Object> getObjectHttpEntity()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    private FinalStandingResponse findRankOfTeam(Country countryResponse, League leagueResponse, Teams teamsResponse, Standings standingsResponse) throws JsonProcessingException, ResourceNotFoundException
    {

        FinalStandingResponse response = new FinalStandingResponse();

        response.setCountryId(countryResponse.getCountryId());
        response.setCountryName(countryResponse.getCountryName());
        response.setLeagueId(leagueResponse.getLeagueId());
        response.setLeagueName(leagueResponse.getLeagueName());
        response.setTeamId(teamsResponse.getTeamKey());
        response.setTeamName(teamsResponse.getTeamName());
        response.setOverAllLeaguePosition(standingsResponse.getOverallLeaguePosition());

        return response;
    }
}