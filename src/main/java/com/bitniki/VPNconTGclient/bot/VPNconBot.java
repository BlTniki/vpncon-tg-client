package com.bitniki.VPNconTGclient.bot;

import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.router.UpdateRouter;
import com.bitniki.VPNconTGclient.exception.UpdateRouterException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * Class that's manipulate with Telegram bot api
 */
public class VPNconBot extends BasicTelegramBot{
    private final UpdateRouter updateRouter;

    public VPNconBot(String botToken, String botUsername, UpdateRouter updateRouter) {
        super(botToken, botUsername);
        this.updateRouter = updateRouter;
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<Response<?>> responses;
        try {
            //get the response that's needs to send
            responses = updateRouter.handle(update);
        } catch (UpdateRouterException e) {
            //Placeholder
            throw new RuntimeException(e);
        }
        //Get all responses
        for(Response<?> response : responses) {
            switch (response.getResponseType()) {
                case SendText -> {
                    try {
                        execute((SendMessage)response.getData());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * sendMessage to chat with given chatId in Response
     */
    private void sendMessage(Long chatId, Response<String> response) {
        String messageData = response.getData();
        SendMessage sendText = new SendMessage(chatId.toString(), messageData);
        sendText.setReplyMarkup(new ForceReplyKeyboard(true));
        try {
            execute(sendText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
