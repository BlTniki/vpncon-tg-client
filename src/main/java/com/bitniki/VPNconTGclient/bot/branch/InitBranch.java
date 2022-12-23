package com.bitniki.VPNconTGclient.bot.branch;

import com.bitniki.VPNconTGclient.bot.Response;
import com.bitniki.VPNconTGclient.bot.ResponseType;
import com.bitniki.VPNconTGclient.bot.Responses;
import com.bitniki.VPNconTGclient.requestEntity.UserEntity;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

import java.util.ArrayList;
import java.util.StringJoiner;

public class InitBranch extends Branch{
    private final String loginText = "Hi! Type your login";
    private final String passwordText = "Great! now type your password";
    private final String entityText = "Awesome! Your user is:\n";
    private UserEntity userEntity;

    public InitBranch(Branch prevBranch) {
        super(prevBranch);
    }

    @Override
    public Responses handle(Update update) {
        Message message = update.getMessage();
        Responses responses = new Responses(message.getChatId());
        responses.setResponseList(new ArrayList<>());
        if(userEntity == null) {
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), loginText);
            sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
            responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
            userEntity = new UserEntity();
        }
        if(message.getReplyToMessage() != null) {
            if(message.getReplyToMessage().getText().equals(loginText)) {
                userEntity.setUsername(message.getText());
                SendMessage sendMessage = new SendMessage(message.getChatId().toString(), passwordText);
                sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
                responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
            }
            if(message.getReplyToMessage().getText().equals(passwordText)) {
                userEntity.setPassword(message.getText());
                SendMessage sendMessage = new SendMessage(message.getChatId().toString(), entityText
                        + userEntity.getUsername()
                        + "\n" + userEntity.getPassword());
                responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
            }
        }
        return responses;
    }
}
