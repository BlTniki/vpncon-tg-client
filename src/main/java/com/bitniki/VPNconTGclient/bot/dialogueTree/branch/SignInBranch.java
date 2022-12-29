package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.bot.response.Responses;
import com.bitniki.VPNconTGclient.exception.RequestServiceException;
import com.bitniki.VPNconTGclient.exception.notFoundException.UserNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

import java.util.ArrayList;

public class AuthBranch extends BranchWithUser{
    private final String loginText = "Введи свой логин";
    private final String passwordText = "Так, а теперь пароль";
    private final String endText = "Нашёл твой аккаунт! вот он:\n";

    public AuthBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Override
    public Responses handle(Update update) {
        //Get message from update
        Message message = update.getMessage();


        //ask login branch state
        if(message.getText().equals("Авторизация")) {
            return askLogin(message);
        }
        //ask password branch state
        if(message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals(loginText)) {
            return askPassword(message);
        }
        //associateUser branch state
        if(message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals(passwordText)) {
            return associateUser(message);
        }
        //if we got here send error
        //Init Responses
        Responses responses = new Responses(message.getChatId());
        responses.setResponseList(new ArrayList<>());
        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                "Что-то я не смог тебя понять. Давай кинем тебя в начало");
        responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        responses.setNextBranch(new InitBranch(this, requestService));
        return responses;
    }

    private Responses askLogin(Message message) {
        //Init Responses
        Responses responses = new Responses(message.getChatId());
        responses.setResponseList(new ArrayList<>());

        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), loginText);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
        responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        this.userEntity = new UserEntity();
        return responses;
    }

    private Responses askPassword(Message message) {
        //Init Responses
        Responses responses = new Responses(message.getChatId());
        responses.setResponseList(new ArrayList<>());

        //Make Response
        userEntity.setLogin(message.getText());
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), passwordText);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
        responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        return responses;
    }

    private Responses associateUser(Message message) {
        //Init Responses
        Responses responses = new Responses(message.getChatId());
        responses.setResponseList(new ArrayList<>());

        //end build entity
        userEntity.setTelegramId(message.getFrom().getId());
        userEntity.setTelegramUsername(message.getFrom().getUserName());
        //try associateTelegramIdWithUser
        try {
            requestService.associateTelegramIdWithUser(userEntity);
        } catch (UserNotFoundException e) {
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                    "Юзера с такими логином и паролем не существует\n" +
                            "Попробуй ещё раз или создай нового\n" +
                            "Накрайняк пиши сюда: @BITniki");
            responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
            responses.setNextBranch(new InitBranch(this, requestService));
            return responses;
//            } catch (UserValidationFailedException e) {
//                SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
//                        "Использовались некоректные символы:\n" +
//                                e.getMessage() +
//                                "\nПопробуй ещё раз\n" +
//                                "Накрайняк пиши сюда: @BITniki");
//                responses.getResponseList().add(new Response(ResponseType.SendText, sendMessage));
        } catch (RequestServiceException e) {
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                    "Похоже на ошбику приложения, напиши мне: @BITniki");
            responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
            responses.setNextBranch(new InitBranch(this, requestService));
            return responses;
        }
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                endText + "\n" + userEntity);
        responses.getResponseList().add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        responses.setNextBranch(new InitBranch(this, requestService));
        return responses;
    }
}
