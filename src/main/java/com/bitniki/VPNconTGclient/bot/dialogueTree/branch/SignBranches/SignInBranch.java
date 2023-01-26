package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SignBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.BranchWithUser;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.InitBranch;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestServiceException;
import com.bitniki.VPNconTGclient.bot.exception.notFoundException.UserNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class SignInBranch extends BranchWithUser {
    private final String loginText = "Введи свой логин";
    private final String passwordText = "Так, а теперь пароль";
    private final String endText = "Нашёл твой аккаунт! вот он:\n";
    private final String signInButtonText = "Авторизация";

    public SignInBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Override
    public List<Response<?>> makeResponses(Update update)
            throws RequestServiceException, BranchBadUpdateProvidedException {
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
        //If we got here return null
        return null;
    }

    private List<Response<?>> askLogin(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), loginText);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
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
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        return responses;
    }

    private List<Response<?>> associateUser(Message message) throws UserNotFoundException, RequestServiceException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //end build entity
        userEntity.setTelegramId(message.getFrom().getId());
        userEntity.setTelegramUsername(message.getFrom().getFirstName());
        //try associateTelegramIdWithUser
        try {
            this.userEntity = requestService.associateTelegramIdWithUser(userEntity);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(
                    """
                        Юзера с такими логином и паролем не существует
                        Попробуй ещё раз или создай нового
                        Накрайняк пиши сюда: @BITniki
                    """
            );
        } catch (RequestServiceException e) {
            throw new RequestServiceException(
                    "Похоже на ошбику приложения, напиши мне: @BITniki"
            );
        }
        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                endText + "\n" + userEntity);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //route to InitBranch
        this.setNextBranch(new InitBranch(this, requestService));
        return responses;
    }
}
