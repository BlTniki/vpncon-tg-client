package com.bitniki.VPNconTGclient.bot.branch;

import com.bitniki.VPNconTGclient.bot.Response;
import com.bitniki.VPNconTGclient.bot.ResponseType;
import com.bitniki.VPNconTGclient.bot.Responses;
import com.bitniki.VPNconTGclient.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.service.RequestService;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class InitBranch extends Branch{
    private final String initText = "Привет! Представься пожалуста.";
    private final String loginText = "Hi! Type your login";
    private final String passwordText = "Great! now type your password";
    private final String entityText = "Awesome! Your user is:\n";
    private UserEntity userEntity;

    public InitBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Override
    public Responses handle(Update update) {
        //Get message from update
        Message message = update.getMessage();
        //Init Responses
        Responses responses = new Responses(message.getChatId());
        responses.setResponseList(new ArrayList<>());

        //init branch state
        //send hi message and set nav buttons
        if(message.getText().equals("/start")) {
            try {
                this.userEntity = getRequestService().getUserByTelegramId(message.getFrom().getId());
                SendMessage sendMessage = new SendMessage(message.getChatId().toString(), entityText
                        + userEntity
                );
                responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
                return responses;
            } catch (HttpClientErrorException e) {
                if(!e.getStatusCode().is4xxClientError()) throw e;
            }
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), initText);
            KeyboardRow keyboardRow = new KeyboardRow(List.of(
                    new KeyboardButton("Регистрация"),
                    new KeyboardButton("Авторизация")
            ));
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardRow));
            sendMessage.setReplyMarkup(replyKeyboardMarkup);

            responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
            userEntity = new UserEntity();
            return responses;
        }
        //login typed branch state
        if(message.getText().equals("Авторизация")) {
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), loginText);
            sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
            responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        }
        //login typed branch state
        if(message.getReplyToMessage()!= null && message.getReplyToMessage().getText().equals(loginText)) {
            userEntity.setLogin(message.getText());
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), passwordText);
            sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
            responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        }
        //password typed branch state
        if(message.getReplyToMessage()!= null && message.getReplyToMessage().getText().equals(passwordText)) {
            userEntity.setPassword(message.getText());
            userEntity.setTelegramId(message.getFrom().getId());
            userEntity.setTelegramUsername(message.getFrom().getUserName());
            try {
                getRequestService().associateTelegramIdWithUser(userEntity);
            } catch (HttpClientErrorException e) {
                if(!e.getStatusCode().is4xxClientError()) throw e;
                SendMessage sendMessage = new SendMessage(message.getChatId().toString(), e.getMessage());
                responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
                return responses;
            }

            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), entityText
                    + userEntity
            );
            responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        }

        return responses;
    }
}
