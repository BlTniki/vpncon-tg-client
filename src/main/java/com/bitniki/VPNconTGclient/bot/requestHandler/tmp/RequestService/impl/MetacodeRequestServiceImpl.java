package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl.MetacodeForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.MetacodeRequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler404Exception;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.RequestHandler;

public class MetacodeRequestServiceImpl implements MetacodeRequestService {
    private final RequestHandler requestHandler;

    public MetacodeRequestServiceImpl(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public String useCodeOnServerByLogin(String login, MetacodeForRequest model) throws ModelNotFoundException {
        final String ENDPOINT = "/metacodes/use/" + login;

        try {
            return requestHandler.POST(ENDPOINT, model, String.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Промокод не валиден");
        }
    }
}
