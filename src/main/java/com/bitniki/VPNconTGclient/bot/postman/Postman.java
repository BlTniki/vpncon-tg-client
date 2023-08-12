package com.bitniki.VPNconTGclient.bot.postman;

import com.bitniki.VPNconTGclient.bot.BasicTelegramBot;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Reminder;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.RequestServiceFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;


@SuppressWarnings("unused")
public class Postman {
    private final BasicTelegramBot bot;
    private final RequestServiceFactory requestService;
    public Postman(BasicTelegramBot bot, RequestServiceFactory requestService) {
        this.bot = bot;
        this.requestService = requestService;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void deliverReminders() {
        //Get mails
        List<Reminder> reminders = requestService.REMINDER_REQUEST_SERVICE.getRemindersFromServer();

        //Send to users
        for (Reminder reminder: reminders) {
            Long telegramId = reminder.getUser().getTelegramId();
            if(telegramId!=null) {
                bot.sendMailToUser(telegramId, reminder.getPayload());
            }
        }
    }
}
