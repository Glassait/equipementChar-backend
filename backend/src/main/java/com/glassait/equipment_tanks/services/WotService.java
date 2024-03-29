package com.glassait.equipment_tanks.services;

import com.glassait.equipment_tanks.abstracts.wot_api.Response;
import com.glassait.equipment_tanks.adapters.MemberDtoAdapter;
import com.glassait.equipment_tanks.api.model.MemberDto;
import com.glassait.equipment_tanks.api.model.MembersDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Service class for interacting with the World of Tanks API.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WotService {
    /**
     * The clan ID of the clan to retrieve information about.
     */
    @Value("${glassait.clan-id}")
    private String clanID;

    /**
     * The base URL for the World of Tanks API requests.
     */
    private static final String WOT_API_URL =
            "https://api.worldoftanks.eu/wot/clans/info/?application_id=d5cfa347c97b1997c50c1797e2f1cfdc&language=fr&clan_id=";

    /**
     * Formats the World of Tanks API URL with the given parameter.
     *
     * @param parameter the parameter to add to the URL
     * @return the formatted URL
     */
    private String formatWoTApiUrl(String parameter) {
        return String.format("%s%s", WOT_API_URL + clanID, parameter);
    }

    /**
     * Creates an HTTP URL connection to the given World of Tanks API URL.
     *
     * @param wotUrl the URL of the API request
     * @return the HTTP URL connection
     * @throws URISyntaxException if the URL is not a valid URI
     * @throws IOException        if there is an error creating the connection
     */
    private HttpURLConnection getHttpURLConnection(String wotUrl) throws URISyntaxException, IOException {
        URI uri = new URI(wotUrl);
        log.info("Call to wot api with url : {}", uri);
        HttpURLConnection httpURLConnection = (HttpURLConnection) uri.toURL().openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(5000);

        return httpURLConnection;
    }

    /**
     * Converts an HTTP URL connection to a World of Tanks API response.
     *
     * @param httpURLConnection the HTTP URL connection
     * @return the World of Tanks API response
     * @throws IOException if there is an error reading from the connection
     */
    private Response convertURLConnectionToResponse(HttpURLConnection httpURLConnection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return new Gson().fromJson(content.toString(), Response.class);
    }

    /**
     * Retrieves information about the members of the clan.
     *
     * @return the members of the clan, or null if there was an error
     */
    public MembersDto getClanMembers() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(MemberDto.class, new MemberDtoAdapter());

        try {
            HttpURLConnection httpURLConnection = this.getHttpURLConnection(formatWoTApiUrl("&fields=members.account_id%2Cmembers.role"));

            int status = httpURLConnection.getResponseCode();
            if (status != 200) {
                log.error("The request to wot api failed for getting all clan members with status : " + status);
                return null;
            }

            Response response = this.convertURLConnectionToResponse(httpURLConnection);
            log.debug("Response : {}", response);
            return builder.create().fromJson(response.getData().get(clanID).toString(), MembersDto.class);
        } catch (URISyntaxException | IOException | JsonSyntaxException e) {
            log.error("The request to wot api failed with error: \n" + e);
            throw new RuntimeException(e); // NOSONAR
        }
    }

    /**
     * Checks if the given access token is valid.
     *
     * @param accessToken the access token to check
     * @return true if the access token is valid, false otherwise
     */
    public boolean checkAccessToken(String accessToken) {
        try {
            HttpURLConnection httpURLConnection = this.getHttpURLConnection(formatWoTApiUrl("&fields=private&access_token=" + accessToken));

            int status = httpURLConnection.getResponseCode();
            log.info("Check access token status : " + status);
            if (status != 200) {
                log.error("The request to wot api failed with the given accessToken with status : " + status);
                return false;
            }

            Response response = this.convertURLConnectionToResponse(httpURLConnection);
            log.info("Response : {}", response);
            return !response.getStatus().equals("error");
        } catch (URISyntaxException | IOException e) {
            log.error("The request to wot api failed with error: " + e);
            return false;
        }
    }
}
