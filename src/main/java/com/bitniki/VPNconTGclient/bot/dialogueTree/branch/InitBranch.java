package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class InitBranch extends BranchWithUser{
    private enum BranchState {
        InitState(),
        WaitingForAuthType();
    }
    private BranchState branchState;
    private final String unrecognizedInitText = "Привет! Представься пожалуста.";
    private final String recognizedInitText = "Привет, ";

    public InitBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Override
    public List<Response<?>> handle(Update update) {
        //Get message from update
        Message message = update.getMessage();
        //Init Responses
        Responses responses = new Responses(message.getChatId());
        responses.setResponseList(new ArrayList<>());

        // Try load user
        boolean isUserLoaded;
        try {
            this.userEntity = loadUserByTelegramId(message.getFrom().getId());
            isUserLoaded = true;
        } catch (Exception e) {
            isUserLoaded = false;
        }

        //greet recognized user
        if(isUserLoaded) {
            //make hi message
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                    recognizedInitText + userEntity.getTelegramUsername()
            );
            responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
            return responses;

        //If we got here send error
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();
        //make response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), errorText);
        responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        //reinitialize branch
        this.setNextBranch(new InitBranch(null, requestService));
        return responses;
    }

    private List<Response<?>> greetRecognizedUser(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();
        //make hi message
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                recognizedInitText + userEntity.getTelegramUsername()
        );
        responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        return responses;
    }

    private List<Response<?>> greetUnrecognizedUser(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //make hi message
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                unrecognizedInitText
        );
        //set nav buttons
        KeyboardRow keyboardRow = new KeyboardRow(List.of(
                new KeyboardButton("Регистрация"),
                new KeyboardButton("Авторизация")
        ));
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardRow));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));

        return responses;
    }
}
