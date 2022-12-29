package com.bitniki.VPNconTGclient.bot.requestHandler;

import com.bitniki.VPNconTGclient.exception.RequestService5xxException;
import com.bitniki.VPNconTGclient.exception.RequestServiceException;
import com.bitniki.VPNconTGclient.exception.notFoundException.UserNotFoundException;
import com.bitniki.VPNconTGclient.exception.validationFailedException.UserValidationFailedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.TokenEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

    public UserEntity getUserByTelegramId(Long telegramId)
            throws RequestServiceException, UserValidationFailedException, UserNotFoundException {
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
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 400)
                throw new UserValidationFailedException(e.getMessage());
            if(e.getStatusCode().value() == 404)
                throw new UserNotFoundException(e.getMessage());
            if(e.getStatusCode().is5xxServerError())
                throw new RequestService5xxException("Problems with server occurred");
            throw e;
        }
        return Objects.requireNonNull(response.getBody());
    }
    public UserEntity associateTelegramIdWithUser(UserEntity userEntity)
            throws UserNotFoundException, RequestService5xxException {
        String uri = this.VPNconAddress + "/auth/tg";
        //Configure request body
        HttpEntity<UserEntity> httpEntity = new HttpEntity<>(
                userEntity,
                makeHttpHeaders()
        );
        //Configure response entity
        ResponseEntity<UserEntity> response;
        RestTemplate restTemplate = new RestTemplate();
        try {
            response = restTemplate.postForEntity(uri, httpEntity, UserEntity.class);
            System.out.println(response);
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 404)
                throw new UserNotFoundException(e.getMessage());
            if(e.getStatusCode().is5xxServerError())
                throw new RequestService5xxException("Problems with server occurred");
            throw e;
        }
        return response.getBody();
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
