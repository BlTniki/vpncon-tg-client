package com.bitniki.VPNconTGclient.bot.branch;

import com.bitniki.VPNconTGclient.bot.Response;
import com.bitniki.VPNconTGclient.bot.ResponseType;
import com.bitniki.VPNconTGclient.bot.Responses;
import com.bitniki.VPNconTGclient.requestEntity.UserEntity;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

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

//    @Override
//    public Responses handle(Update update) {
//        Message message = update.getMessage();
//        Responses responses = new Responses(message.getChatId());
//        responses.setResponseList(new ArrayList<>());
//        if(message.isCommand() && message.getText().equals("/start")) {
//            responses.getResponseList().add(new Response<String>(ResponseType.SendText, "hi"));
//        } else {
//            responses.getResponseList().add(new Response<String>(ResponseType.SendText, message.getText()));
//        }
//        responses.setNextBranch(new PiBranch(this));
//        return responses;
//    }
    @Override
    public Responses handle(Update update) {
        Message message = update.getMessage();
        Responses responses = new Responses(message.getChatId());
        responses.setResponseList(new ArrayList<>());
        if(userEntity == null) {
            responses.getResponseList().add(new Response<String>(ResponseType.SendText, loginText));
            userEntity = new UserEntity();
        }
        if(message.getReplyToMessage() != null) {
            if(message.getReplyToMessage().getText().equals(loginText)) {
                userEntity.setUsername(message.getText());
                responses.getResponseList().add(new Response<String>(ResponseType.SendText, passwordText));
            }
            if(message.getReplyToMessage().getText().equals(passwordText)) {
                userEntity.setPassword(message.getText());
                responses.getResponseList().add(new Response<String>(ResponseType.SendText, entityText + userEntity.getUsername() + "\n" + userEntity.getPassword()));
            }
        }
        return responses;
    }
}
