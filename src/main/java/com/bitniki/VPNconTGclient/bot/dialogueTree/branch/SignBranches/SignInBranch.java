package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SignBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.AuthBranch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.InitBranch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.validationFailedException.EntityValidationFailedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.UserEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl.UserForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelValidationFailedException;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class SignInBranch extends AuthBranch {
    private enum BranchState {
        InitState(),
        WaitForLogin(),
        WaitForPassword()
    }
    private BranchState branchState;
    private UserEntity userEntity;
    private UserForRequest credentials;
    private final String loginText = "Введи свой логин";
    private final String passwordText = "Так, а теперь пароль";
    private final String endText = "Нашёл твой аккаунт! вот он:\n";

    public SignInBranch(Branch prevBranch, RequestServiceFactory requestService) {
        super(prevBranch, requestService);
        this.branchState = BranchState.InitState;
    }

    @Override
    public List<Response<?>> makeResponses(Update update)
            throws BranchCriticalException {
        //Get message from update
        Message message = update.getMessage();

        //InitState State
        if(branchState.equals(BranchState.InitState)) {
            return askLogin(message);
        }
        //ask password branch state
        if(branchState.equals(BranchState.WaitForLogin)) {
            return askPassword(message);
        }
        //associateUser branch state
        if(branchState.equals(BranchState.WaitForPassword)) {
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
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        // Init entities
        this.userEntity = new UserEntity();
        this.credentials = new UserForRequest();

        //Change Branch state
        branchState = BranchState.WaitForLogin;
        return responses;
    }

    private List<Response<?>> askPassword(Message message) throws BranchBadUpdateProvidedException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        // set login
        String login = getTextFrom(message);
        this.credentials.setLogin(login);

        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), passwordText);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Change Branch state
        branchState = BranchState.WaitForPassword;
        return responses;
    }

    private List<Response<?>> associateUser(Message message)
            throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        // set password
        String password = getTextFrom(message);
        this.credentials.setPassword(password);

        // check auth credentials
        if (! requestService.USER_REQUEST_SERVICE.isSignInValid(this.credentials)) {
            throw new EntityValidationFailedException(
                    """
                        Юзера с такими логином и паролем не существует
                        Попробуй ещё раз или создай нового
                        Накрайняк пиши сюда: @BITniki
                    """
            );
        }

        // set tg
        this.credentials.setTelegramId(message.getFrom().getId());
        this.credentials.setTelegramNickname(message.getFrom().getUserName());

        //try associate TelegramId With User
        try {
            this.userEntity = requestService.USER_REQUEST_SERVICE.associateTelegramIdWithUser(credentials);
        } catch (ModelValidationFailedException | ModelNotFoundException e) {
            throw new BranchCriticalException(
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
