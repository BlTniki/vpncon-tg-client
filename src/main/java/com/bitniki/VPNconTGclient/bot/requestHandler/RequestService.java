package com.bitniki.VPNconTGclient.bot.requestHandler;

import com.bitniki.VPNconTGclient.bot.exception.notFoundException.*;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.*;
import com.bitniki.VPNconTGclient.bot.exception.validationFailedException.*;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    public List<HostEntity> getHostsFromServer() throws RequestService5xxException {
        String uri = this.VPNconAddress + "/hosts";
        //Configure response entity
        ResponseEntity<HostEntity[]> response;

        //Make request
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    HostEntity[].class
            );
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().is5xxServerError())
                throw new RequestService5xxException("Problems with server occurred");
            throw e;
        }

        //return wrapped array in List
        if(response.getBody() == null) {
            //Just empty list
            return new ArrayList<>();
        }
        return List.of(response.getBody());
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

    public InputFile getFileFromServer(PeerEntity peerEntity)
            throws RequestService5xxException, PeerNotFoundException {
        //Get token for download
        String confToken = getTokenForDownload(peerEntity);

        //Build URL
        URL url;
        try {
            url = new URL(
                    "http://"
                            + peerEntity.getHost().getIpadress()
                            + "/api/1.0/conf/"
                            + confToken
                    );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        //Open stream to config file
        InputStream inputStream;
        try {
            inputStream = url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Return file
        return new InputFile(inputStream, peerEntity.getPeerConfName());
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

    private String getTokenForDownload(PeerEntity peerEntity) throws PeerNotFoundException, RequestService5xxException {
        String uri = this.VPNconAddress + "/peers/conf/" + peerEntity.getId();
        //Configure response entity
        ResponseEntity<String> response;

        //Make request
        try {
            response = restTemplate.exchange(uri,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    String.class
            );
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 404)
                throw new PeerNotFoundException(e.getMessage());
            if(e.getStatusCode().is5xxServerError())
                throw new RequestService5xxException("Problems with server occurred");
            throw e;
        }
        return response.getBody();
    }
}
