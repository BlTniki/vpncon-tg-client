package com.bitniki.VPNconTGclient.bot;

import org.telegram.telegrambots.meta.api.objects.Update;

public class ConsoleEchoBot extends BasicTelegramBot {

    public ConsoleEchoBot(String botToken, String botUsername) {
        super(botToken, botUsername);
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        System.out.println(update.getMessage().getText());
    }

}
