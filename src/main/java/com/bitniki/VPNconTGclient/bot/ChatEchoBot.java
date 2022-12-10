package com.bitniki.VPNconTGclient.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ChatEchoBot extends BasicTelegramBot{
    public ChatEchoBot(String botToken, String botUsername) {
        super(botToken, botUsername);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage(update.getMessage().getChatId().toString(),
                                                  update.getMessage().getText());
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
