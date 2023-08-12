package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Reminder;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.ReminderRequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.RequestHandler;

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
