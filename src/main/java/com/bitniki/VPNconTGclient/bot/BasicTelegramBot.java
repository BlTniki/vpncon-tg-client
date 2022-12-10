package com.bitniki.VPNconTGclient.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Basic telegram bot, it's cant handle updates
 * but can register bot and handle token and username
 */
public abstract class BasicTelegramBot extends TelegramLongPollingBot {
    private String botToken;

    private String botUsername;

    public BasicTelegramBot() {
    }

    public BasicTelegramBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

}
