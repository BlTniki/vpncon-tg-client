package com.bitniki.VPNconTGclient.bot.request.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.request.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.Host;
import com.bitniki.VPNconTGclient.bot.request.RequestService.HostRequestService;
import com.bitniki.VPNconTGclient.bot.request.exception.requestHandler.RequestHandler404Exception;
import com.bitniki.VPNconTGclient.bot.request.requestHandle.RequestHandler;

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

    @Override
    public Integer countAvailablePeersOnHost(Long hostId) throws ModelNotFoundException {
        final String ENDPOINT = "/hosts/count/" + hostId;

        try {
            return requestHandler.GET(ENDPOINT, Integer.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти хост с id %d".formatted(hostId));
        }
    }
}
