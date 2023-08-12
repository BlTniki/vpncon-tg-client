package com.bitniki.VPNconTGclient.bot.request.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.request.Model.impl.Reminder;
import com.bitniki.VPNconTGclient.bot.request.RequestService.ReminderRequestService;
import com.bitniki.VPNconTGclient.bot.request.requestHandle.RequestHandler;

import java.util.List;

public class ReminderRequestServiceImpl implements ReminderRequestService {
    private final RequestHandler requestHandler;

    public ReminderRequestServiceImpl(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public List<Reminder> getRemindersFromServer() {
        final String ENDPOINT = "/reminders/tg";

        return List.of(requestHandler.GET(ENDPOINT, Reminder[].class));
    }
}
