package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.PaymentRequestService;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

public class PaymentRequestServiceImpl implements PaymentRequestService {
    private final String VPNCON_ADDRESS;

    public PaymentRequestServiceImpl(String VPNCON_ADDRESS) {
        this.VPNCON_ADDRESS = VPNCON_ADDRESS;
    }

    @Override
    public String makePaymentUrl(Long userId, Long subsId) {
        try{
            return new URIBuilder(VPNCON_ADDRESS + "/create")
                    .addParameter("subscriptionId", subsId.toString())
                    .addParameter("userId", userId.toString())
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}
