package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.exception.RequestServiceException;
import com.bitniki.VPNconTGclient.exception.notFoundException.UserNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

import java.util.ArrayList;
import java.util.List;

public class SignInBranch extends BranchWithUser{
    private final String loginText = "Введи свой логин";
    private final String passwordText = "Так, а теперь пароль";
    private final String endText = "Нашёл твой аккаунт! вот он:\n";
    private final String signInButtonText = "Авторизация";

    public SignInBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Override
    public List<Response<?>> handle(Update update) {
        //Get message from update
        Message message = update.getMessage();


        //ask login branch state
        if(message.getText().equals(signInButtonText)) {
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
        List<Response<?>> responses = new ArrayList<>();
        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                "Что-то я не смог тебя понять. Давай кинем тебя в начало");
        responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        //route to InitBranch
        this.setNextBranch(new InitBranch(this, requestService));
        return responses;
    }

    private List<Response<?>> askLogin(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), loginText);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
        responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        this.userEntity = new UserEntity();
        return responses;
    }

    private List<Response<?>> askPassword(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Make Response
        userEntity.setLogin(message.getText());
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), passwordText);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
        responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        return responses;
    }

    private List<Response<?>> associateUser(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

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
            responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
            //route to InitBranch
            this.setNextBranch(new InitBranch(this, requestService));
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
            responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
            //route to InitBranch
            this.setNextBranch(new InitBranch(this, requestService));
            return responses;
        }
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                endText + "\n" + userEntity);
        responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        //route to InitBranch
        this.setNextBranch(new InitBranch(this, requestService));
        return responses;
    }
}
