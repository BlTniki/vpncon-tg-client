package com.bitniki.VPNconTGclient.bot.request.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.request.enumTypes.Role;
import com.bitniki.VPNconTGclient.bot.request.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.request.exception.requestHandler.RequestHandler404Exception;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.Subscription;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.UserSubscription;
import com.bitniki.VPNconTGclient.bot.request.RequestService.SubscriptionRequestService;
import com.bitniki.VPNconTGclient.bot.request.requestHandle.RequestHandler;

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
