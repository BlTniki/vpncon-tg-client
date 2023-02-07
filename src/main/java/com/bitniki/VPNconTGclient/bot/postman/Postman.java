package com.bitniki.VPNconTGclient.bot.postman;

import com.bitniki.VPNconTGclient.bot.BasicTelegramBot;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.MailEntity;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;


@SuppressWarnings("unused")
public class Postman {
    private final BasicTelegramBot bot;
    private final RequestService requestService;
    public Postman(BasicTelegramBot bot, RequestService requestService) {
        this.bot = bot;
        this.requestService = requestService;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void deliverMails() throws RequestService5xxException {
        //Get mails
        List<MailEntity> mails = requestService.getMailsFromServer();

        //Send to users
        for (MailEntity mail: mails) {
            Long telegramId = mail.getUser().getTelegramId();
            if(telegramId!=null) {
                bot.sendMailToUser(telegramId, mail.getPayload());
            }
        }
    }
}
