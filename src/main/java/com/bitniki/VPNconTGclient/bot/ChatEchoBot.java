package com.bitniki.VPNconTGclient.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;

public class ChatEchoBot extends BasicTelegramBot{
    public ChatEchoBot(String botToken, String botUsername) {
        super(botToken, botUsername);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage(update.getMessage().getChatId().toString(),
                                                  update.getMessage().getText());
            System.out.println(update.toString().replaceAll("[a-zA-Z]+=null", ""));
            System.out.println(update.getMessage().getText());
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton("lol"));
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(Collections.singletonList(keyboardRow));
            message.setReplyMarkup(replyKeyboardMarkup);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
