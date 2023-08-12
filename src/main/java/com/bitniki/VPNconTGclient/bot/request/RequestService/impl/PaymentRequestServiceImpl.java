package com.bitniki.VPNconTGclient.bot.request.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.request.RequestService.PaymentRequestService;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

public class PaymentRequestServiceImpl implements PaymentRequestService {
    private final String VPNCON_DOMAIN;

    public PaymentRequestServiceImpl(String VPNCON_DOMAIN) {
        this.VPNCON_DOMAIN = VPNCON_DOMAIN;
    }

    @Override
    public String makePaymentUrl(Long userId, Long subsId) {
        try{
            return new URIBuilder(VPNCON_DOMAIN + "/payments/create")
                    .addParameter("subscriptionId", subsId.toString())
                    .addParameter("userId", userId.toString())
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}
