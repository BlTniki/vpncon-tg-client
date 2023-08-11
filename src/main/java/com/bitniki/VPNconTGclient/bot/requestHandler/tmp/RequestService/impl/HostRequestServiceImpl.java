package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Host;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.HostRequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandlerException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.RequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class HostRequestServiceImpl implements HostRequestService {
    private final RequestHandler requestHandler;

    public HostRequestServiceImpl(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public List<Host> getHostsFromServer() {
        final String ENDPOINT = "/hosts";

        try {
            return List.of(requestHandler.GET(ENDPOINT, Host[].class));
        } catch (RequestHandlerException e) {
            log.error("Неожиданная ошибка при попытке получить юзера по логину:\n" + e.getMessage());
            throw e;
        }
    }
}
