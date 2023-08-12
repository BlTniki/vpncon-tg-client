package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Host;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.HostRequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.RequestHandler;

import java.util.List;

public class HostRequestServiceImpl implements HostRequestService {
    private final RequestHandler requestHandler;

    public HostRequestServiceImpl(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public List<Host> getHostsFromServer() {
        final String ENDPOINT = "/hosts";

        return List.of(requestHandler.GET(ENDPOINT, Host[].class));
    }
}
