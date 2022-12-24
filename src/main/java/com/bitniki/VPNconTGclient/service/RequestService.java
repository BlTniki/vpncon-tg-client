package com.bitniki.VPNconTGclient.service;

import com.bitniki.VPNconTGclient.requestEntity.TokenEntity;
import com.bitniki.VPNconTGclient.requestEntity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Objects;

public class RequestService {
    private final String VPNconAddress;
    private final String botLogin;
    private final String botPassword;
    private String botToken;

    public RequestService(String VPNconAddress, String botLogin, String botPassword) {
        this.VPNconAddress = VPNconAddress;
        this.botLogin = botLogin;
        this.botPassword = botPassword;
        this.botToken = SignInAndReturnToken();
    }

    public UserEntity getUserByTelegramId(Long telegramId) {
        String uri = this.VPNconAddress + "/users/tg/" + telegramId;
        //Configure response entity
        ResponseEntity<UserEntity> response;
        RestTemplate restTemplate = new RestTemplate();
        try {
            response = restTemplate.exchange(uri,
                    HttpMethod.GET,
                    new HttpEntity<>(makeHttpHeaders()),
                    UserEntity.class
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return Objects.requireNonNull(response.getBody());

    }
    public void associateTelegramIdWithUser(UserEntity userEntity) {
        String uri = this.VPNconAddress + "/auth/tg";
        //Configure request body
        HttpEntity<UserEntity> httpEntity = new HttpEntity<>(
                userEntity,
                makeHttpHeaders()
        );
        //Configure response entity
        ResponseEntity<String> response;
        RestTemplate restTemplate = new RestTemplate();
        try {
            response = restTemplate.postForEntity(uri, httpEntity, String.class);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    private String SignInAndReturnToken() {
        String uri = this.VPNconAddress + "/auth/login";
        //Configure request body
        HttpEntity<UserEntity> httpEntity = new HttpEntity<>(
                new UserEntity(this.botLogin, this.botPassword)
        );
        //Configure response entity
        ResponseEntity<TokenEntity> response;
        RestTemplate restTemplate = new RestTemplate();
        try {
            response = restTemplate.postForEntity(uri, httpEntity, TokenEntity.class);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return Objects.requireNonNull(response.getBody()).getToken();
    }
    private HttpHeaders makeHttpHeaders() {
        //set auth header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.botToken);
        return headers;
    }
}
