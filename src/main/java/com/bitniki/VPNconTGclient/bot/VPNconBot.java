package com.bitniki.VPNconTGclient.bot;

import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.bot.router.UpdateRouter;
import com.bitniki.VPNconTGclient.bot.exception.UpdateRouterException;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * Class that's manipulate with Telegram bot api
 */
public class VPNconBot extends BasicTelegramBot{
    private final String paymentToken;
    private final UpdateRouter updateRouter;

    public VPNconBot(String botToken, String botUsername, String paymentToken, UpdateRouter updateRouter) {
        super(botToken, botUsername);
        this.paymentToken = paymentToken;
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
        try {
            for (Response<?> response : responses) {
                execute(response.getResponseType(), response.getData());
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the appropriate Telegram API method based on the provided {@link ResponseType}
     *
     * @param responseType The type of response to be executed
     * @param data The data associated with the response type
     * @throws TelegramApiException If there is an error executing the Telegram API method
     */
    private <T> void execute(ResponseType responseType, T data) throws TelegramApiException {
        switch (responseType) {
            case SendText -> execute((SendMessage) data);
            case SendPhoto -> execute((SendPhoto) data);
            case SendDoc -> execute((SendDocument) data);
            case SendInvoice -> {
                //set providerToken
                ((SendInvoice) data).setProviderToken(paymentToken);
                sendInvoice((SendInvoice) data);
            }
            case SendAnswerToInvoice -> execute((AnswerPreCheckoutQuery) data);
        }
    }

    /**
     * sendMessage to chat with given chatId in Response
     */
    private void sendInvoice(SendInvoice invoice) {
        invoice.setProviderToken(paymentToken);
        try {
            execute(invoice);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
