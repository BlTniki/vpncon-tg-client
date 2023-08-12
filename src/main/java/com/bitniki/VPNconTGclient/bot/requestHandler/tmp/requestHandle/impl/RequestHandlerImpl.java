package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.Model;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Token;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.ModelForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl.UserForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.RequestHandler;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler400Exception;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler404Exception;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler5xxException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandlerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class RequestHandlerImpl implements RequestHandler {
    private final String VPNCON_ADDRESS;
    private final String BOT_LOGIN;
    private final String BOT_PASSWORD;

    private String botToken;
    private HttpHeaders httpHeaders;

    private final RestTemplate restTemplate;

    /**
     *
     * @param VPNCON_ADDRESS Адрес до сервера в формате http(s)://address
     * @param BOT_LOGIN Логин аккаунта бота на сервере.
     * @param BOT_PASSWORD Бота аккаунта бота на сервере.
     */
    public RequestHandlerImpl(String VPNCON_ADDRESS, String BOT_LOGIN, String BOT_PASSWORD) {
        this.VPNCON_ADDRESS = VPNCON_ADDRESS;
        this.BOT_LOGIN = BOT_LOGIN;
        this.BOT_PASSWORD = BOT_PASSWORD;

        this.restTemplate = new RestTemplate();
    }

    /**
     * Создание хэдеров с токеном аутентификации.
     * @return HttpHeader with auth header
     */
    private HttpHeaders makeHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        //set auth header
        headers.set("Authorization", "Bearer " + this.botToken);
        return headers;
    }

    /**
     * Создание сущности HTTP запроса c телом запроса.
     * @param modelForRequest — Тело реквеста.
     * @return Built HttpEntity
     */
    private <K extends ModelForRequest> HttpEntity<K> makeHttpEntityWithBody(K modelForRequest) {
        return new HttpEntity<>(
                modelForRequest,
                httpHeaders
        );
    }

    public void SignInAndMakeHeaders() throws RequestHandlerException {
        String endpoint = "/users/login";

        // set temp headers
        this.httpHeaders = new HttpHeaders();

        //Configure request body
        UserForRequest userForRequest = UserForRequest.builder()
                .login(this.BOT_LOGIN)
                .password(this.BOT_PASSWORD)
                .build();

        //Make request
        Token token = POST(endpoint, userForRequest, Token.class);

        // set token and make headers
        this.botToken = token.getToken();
        this.httpHeaders = makeHttpHeaders();
    }

    @Override
    public <T> T GET(String endpoint, Class<T> responseBodyClass) throws RequestHandlerException {
        final String URI = this.VPNCON_ADDRESS + endpoint;

        try {
            return restTemplate.exchange(
                    URI,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    responseBodyClass
            ).getBody();
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 400)
                throw new RequestHandler400Exception(e.getMessage());
            if(e.getStatusCode().value() == 404)
                throw new RequestHandler404Exception(e.getMessage());
            if(e.getStatusCode().is5xxServerError()) {
                log.error("Поймал неожиданную ошибку при попытке совершить GET запрос на " + URI + "\n\n" + e);
                throw new RequestHandler5xxException("Problems with server occurred");
            }
            throw e;
        }
    }

    @Override
    public <T extends Model> T DELETE(String endpoint, Class<T> responseBodyClass) throws RequestHandlerException {
        final String URI = this.VPNCON_ADDRESS + endpoint;

        try {
            return restTemplate.exchange(
                    URI,
                    HttpMethod.DELETE,
                    new HttpEntity<>(httpHeaders),
                    responseBodyClass
            ).getBody();
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 400)
                throw new RequestHandler400Exception(e.getMessage());
            if(e.getStatusCode().value() == 404)
                throw new RequestHandler404Exception(e.getMessage());
            if(e.getStatusCode().is5xxServerError()) {
                log.error("Поймал неожиданную ошибку при попытке совершить DELETE запрос на " + URI + "\n\n" + e);
                throw new RequestHandler5xxException("Problems with server occurred");
            }
            throw e;
        }
    }

    @Override
    public <K extends ModelForRequest, T> T POST(String endpoint, K requestBody, Class<T> responseBodyClass)
            throws RequestHandlerException {
        final String URI = this.VPNCON_ADDRESS + endpoint;

        try {
            return restTemplate.exchange(
                    URI,
                    HttpMethod.POST,
                    makeHttpEntityWithBody(requestBody),
                    responseBodyClass
            ).getBody();
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 400)
                throw new RequestHandler400Exception(e.getMessage());
            if(e.getStatusCode().value() == 404)
                throw new RequestHandler404Exception(e.getMessage());
            if(e.getStatusCode().is5xxServerError()) {
                log.error("Поймал неожиданную ошибку при попытке совершить POST запрос на " + URI + "\n\n" + e);
                throw new RequestHandler5xxException("Problems with server occurred");
            }
            throw e;
        }
    }

    @Override
    public <K extends ModelForRequest, T extends Model> T PUT(String endpoint, K requestBody, Class<T> responseBodyClass)
            throws RequestHandlerException {
        final String URI = this.VPNCON_ADDRESS + endpoint;

        try {
            return restTemplate.exchange(
                    URI,
                    HttpMethod.PUT,
                    makeHttpEntityWithBody(requestBody),
                    responseBodyClass
            ).getBody();
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 400)
                throw new RequestHandler400Exception(e.getMessage());
            if(e.getStatusCode().value() == 404)
                throw new RequestHandler404Exception(e.getMessage());
            if(e.getStatusCode().is5xxServerError()) {
                log.error("Поймал неожиданную ошибку при попытке совершить GET запрос на " + URI + "\n\n" + e);
                throw new RequestHandler5xxException("Problems with server occurred");
            }
            throw e;
        }
    }
}
