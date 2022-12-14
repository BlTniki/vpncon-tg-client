package com.bitniki.VPNconTGclient.bot;

import com.bitniki.VPNconTGclient.exception.UpdateRouterException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
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
        Response response;
        try {
            //get the response that's needs to send
            response = updateRouter.handle(update);
        } catch (UpdateRouterException e) {
            //Placeholder
            throw new RuntimeException(e);
        }
        //Bat realization
        //Get all responses
        for(ResponseType responseType: response.getResponses().keySet()) {
            switch (responseType) {
                case SendMessage -> sendMessage(response);
            }
        }
    }

    /**
     * sendMessage to chat with given chatId in Response
     */
    private void sendMessage(Response response) {
        String responseData = (String) response.getResponses().get(ResponseType.SendMessage);
        SendMessage message = new SendMessage(response.getChatId().toString(),
                responseData);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
