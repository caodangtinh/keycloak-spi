package com.voc.service;

import com.voc.dto.JWTToken;
import com.voc.dto.UserSPIResponse;
import com.voc.dto.VerifyPasswordResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class UserService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String VOC_API_WEB_USERS_SPI = "https://api.voc.dev/voc/api/web/users/spi/";
    public static final String KEYCLOAK_TOKEN_ENDPOINT = "https://keycloak.voc.dev/auth/realms/voc-backend/protocol/openid-connect/token";

    public UserSPIResponse getUserDetails(String username) {
        Unirest.setTimeouts(0, 0);
        JWTToken jwtToken = getJwtToken();
        HttpResponse<String> userSPIResponseStr;
        try {
            userSPIResponseStr = Unirest.get(VOC_API_WEB_USERS_SPI + username)
                    .header("Authorization", "Bearer " + jwtToken.getAccess_token())
                    .asString();
            if (userSPIResponseStr.getStatus() != 200) {
                return null;
            }
            return OBJECT_MAPPER.readValue(userSPIResponseStr.getBody(), UserSPIResponse.class);
        } catch (UnirestException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JWTToken getJwtToken() {
        HttpResponse<String> response;
        try {
            response = Unirest.post(KEYCLOAK_TOKEN_ENDPOINT)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("grant_type", "password")
                    .field("username", "voc-backend")
                    .field("password", "vocbackend")
                    .field("client_id", "voc-backend-client")
                    .field("client_secret", "19a2qFmzsnVmrkmvob06ntjtQz4YXZlY")
                    .field("scope", "openid")
                    .asString();
            return OBJECT_MAPPER.readValue(response.getBody(), JWTToken.class);
        } catch (UnirestException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public VerifyPasswordResponse verifyUserPassword(String username, String inputPassword) {
        Unirest.setTimeouts(0, 0);
        JWTToken jwtToken = getJwtToken();
        HttpResponse<String> userSPIResponseStr;
        try {
            userSPIResponseStr = Unirest.get(VOC_API_WEB_USERS_SPI + username + "/" + inputPassword)
                    .header("Authorization", "Bearer " + jwtToken.getAccess_token())
                    .asString();
            if (userSPIResponseStr.getStatus() != 200) {
                return null;
            }
            return OBJECT_MAPPER.readValue(userSPIResponseStr.getBody(), VerifyPasswordResponse.class);
        } catch (UnirestException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
