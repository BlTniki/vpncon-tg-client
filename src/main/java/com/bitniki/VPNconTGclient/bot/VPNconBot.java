package com.bitniki.VPNconTGclient.bot;

import com.bitniki.VPNconTGclient.exception.UpdateRouterException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Class that's manipulate with Telegram bot api
 */
public class VPNconBot extends BasicTelegramBot{
    private final UpdateRouter updateRouter;

    public VPNconBot(String botToken, String botUsername) {
        super(botToken, botUsername);
        this.updateRouter = new UpdateRouter();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Responses responses;
        try {
            //get the response that's needs to send
            responses = updateRouter.handle(update);
        } catch (UpdateRouterException e) {
            //Placeholder
            throw new RuntimeException(e);
        }
        //Bat realization
        //Get all responses
        Long chatId = responses.getChatId();
        for(Response<?> response : responses.getResponseList()) {
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