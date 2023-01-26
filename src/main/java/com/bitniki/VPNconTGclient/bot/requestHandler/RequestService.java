package com.bitniki.VPNconTGclient.bot.requestHandler;

import com.bitniki.VPNconTGclient.bot.exception.notFoundException.EntityNotFoundException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestServiceException;
import com.bitniki.VPNconTGclient.bot.exception.notFoundException.UserNotFoundException;
import com.bitniki.VPNconTGclient.bot.exception.validationFailedException.EntityValidationFailedException;
import com.bitniki.VPNconTGclient.bot.exception.validationFailedException.UserValidationFailedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.PeerEntity;
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
    private final String botToken;
    private final HttpHeaders httpHeaders;
    private final RestTemplate restTemplate;

    public RequestService(String VPNconAddress, String botLogin, String botPassword) {
        this.VPNconAddress = VPNconAddress;
        this.botLogin = botLogin;
        this.botPassword = botPassword;
        this.restTemplate = new RestTemplate();
        this.botToken = SignInAndReturnToken();
        this.httpHeaders = makeHttpHeaders();
    }

    public UserEntity getUserByTelegramId(Long telegramId)
            throws RequestServiceException, UserValidationFailedException, UserNotFoundException {
        String uri = this.VPNconAddress + "/users/tg/" + telegramId;
        //Configure response entity
        ResponseEntity<UserEntity> response;

        //Make request
        try {
            response = restTemplate.exchange(uri,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
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
        HttpEntity<UserEntity> httpEntity = makeHttpEntity(userEntity);
        //Configure response entity
        ResponseEntity<UserEntity> response;


        //Make request
        try {
            response = restTemplate.postForEntity(uri, httpEntity, UserEntity.class);
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 404)
                throw new UserNotFoundException(e.getMessage());
            if(e.getStatusCode().is5xxServerError())
                throw new RequestService5xxException("Problems with server occurred");
            throw e;
        }
        return response.getBody();
    }

    public UserEntity createUserOnServer(UserEntity userEntity)
            throws UserValidationFailedException, RequestService5xxException {
        String uri = this.VPNconAddress + "/users";
        //Configure request body
        HttpEntity<UserEntity> httpEntity = makeHttpEntity(userEntity);
        //Configure response entity
        ResponseEntity<UserEntity> response;

        //Make request
        try {
            response = restTemplate.postForEntity(
                    uri,
                    httpEntity,
                    UserEntity.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 400)
                throw new UserValidationFailedException(e.getMessage());
            if(e.getStatusCode().is5xxServerError())
                throw new RequestService5xxException("Problems with server occurred");
            throw e;
        }
        return response.getBody();
    }

    public PeerEntity createPeerOnServer(PeerEntity peerEntity, Long userId, Long hostId)
            throws EntityNotFoundException, EntityValidationFailedException, RequestService5xxException {
        String uri = this.VPNconAddress + "/peers?user_id=" + userId + "&host_id=" + hostId;
        //Configure request body
        HttpEntity<PeerEntity> httpEntity = makeHttpEntity(peerEntity);
        //Configure response entity
        ResponseEntity<PeerEntity> response;

        //Make request
        try {
            response = restTemplate.postForEntity(
                    uri,
                    httpEntity,
                    PeerEntity.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 400)
                throw new EntityValidationFailedException(e.getMessage());
            if(e.getStatusCode().value() == 404)
                throw new EntityNotFoundException(e.getMessage());
            if(e.getStatusCode().is5xxServerError())
                throw new RequestService5xxException("Problems with server occurred");
            throw e;
        }
        return response.getBody();
    }

    /*
    Make request for auth token and save it
     */
    private String SignInAndReturnToken() {
        String uri = this.VPNconAddress + "/auth/login";
        //Configure request body
        HttpEntity<UserEntity> httpEntity = new HttpEntity<>(
                new UserEntity(this.botLogin, this.botPassword)
        );
        //Configure response entity
        ResponseEntity<TokenEntity> response;

        //Make request
        try {
            response = restTemplate.postForEntity(uri, httpEntity, TokenEntity.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return Objects.requireNonNull(response.getBody()).getToken();
    }

    private HttpHeaders makeHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        //set auth header
        headers.set("Authorization", "Bearer " + this.botToken);
        return headers;
    }

    /*
    Configure request body for user entity
     */
    private HttpEntity<UserEntity> makeHttpEntity(UserEntity entity) {
        return new HttpEntity<>(
                entity,
                httpHeaders
        );
    }
    private HttpEntity<PeerEntity> makeHttpEntity(PeerEntity entity) {
        return new HttpEntity<>(
                entity,
                httpHeaders
        );
    }
}
