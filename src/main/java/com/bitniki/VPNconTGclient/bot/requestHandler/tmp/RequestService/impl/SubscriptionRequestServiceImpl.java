package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Subscription;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.UserSubscription;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.SubscriptionRequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.enumTypes.Role;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler404Exception;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.RequestHandler;

import java.util.List;

public class SubscriptionRequestServiceImpl implements SubscriptionRequestService {
    private final RequestHandler requestHandler;

    public SubscriptionRequestServiceImpl(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public List<Subscription> getSubsByRole(Role role) {
        final String ENDPOINT = "/subs/byRole/" + role;

        return List.of(requestHandler.GET(ENDPOINT, Subscription[].class));
    }

    @Override
    public UserSubscription getUserSubscriptionByUserId(Long userId) throws ModelNotFoundException {
        final String ENDPOINT = "/subs/" + userId;

        try {
            return requestHandler.GET(ENDPOINT, UserSubscription.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException(e.getMessage());
        }
    }
}
