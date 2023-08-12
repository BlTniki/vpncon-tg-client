package com.bitniki.VPNconTGclient.bot.request.RequestService;

import com.bitniki.VPNconTGclient.bot.request.RequestService.impl.*;
import com.bitniki.VPNconTGclient.bot.request.requestHandle.RequestHandler;

/**
 * Класс, который хранит ссылки на все сервисы.
 */
public class RequestServiceFactory {
    public final UserRequestService USER_REQUEST_SERVICE;
    public final HostRequestService HOST_REQUEST_SERVICE;
    public final PeerRequestService PEER_REQUEST_SERVICE;
    public final SubscriptionRequestService SUBS_REQUEST_SERVICE;
    public final MetacodeRequestService METACODE_REQUEST_SERVICE;
    public final PaymentRequestService PAYMENT_REQUEST_SERVICE;
    public final ReminderRequestService REMINDER_REQUEST_SERVICE;

    public RequestServiceFactory(RequestHandler requestHandler, String VPNCON_ADDRESS) {
        USER_REQUEST_SERVICE = new UserRequestServiceImpl(requestHandler);
        HOST_REQUEST_SERVICE = new HostRequestServiceImpl(requestHandler);
        PEER_REQUEST_SERVICE = new PeerRequestServiceImpl(requestHandler);
        SUBS_REQUEST_SERVICE = new SubscriptionRequestServiceImpl(requestHandler);
        METACODE_REQUEST_SERVICE = new MetacodeRequestServiceImpl(requestHandler);
        PAYMENT_REQUEST_SERVICE = new PaymentRequestServiceImpl(VPNCON_ADDRESS);
        REMINDER_REQUEST_SERVICE = new ReminderRequestServiceImpl(requestHandler);
    }
}
