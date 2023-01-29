package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SignBranches.SignInBranch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SignBranches.SignUpBranch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.util.ArrayList;
import java.util.List;


@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class InitBranch extends BranchWithUser{
    private enum BranchState {
        InitState(),
        WaitingForAuthType()
    }
    private BranchState branchState;
    private final String unrecognizedInitText = "Привет! Представься пожалуста.";
    private final String recognizedInitText = "Привет, ";

    private final String errorText = "Похоже на внутренюю ошибку бота. " +
                                     "Напиши мне: @BITniki";
    private final String regButtonText = "Регистрация";
    private final String authButtonText = "Авторизация";

    public InitBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
        this.branchState = BranchState.InitState;
    }

    @Override
    public List<Response<?>> makeResponses(Update update)
            throws BranchBadUpdateProvidedException {
        //Get message from update
        Message message = update.getMessage();

        //Init state
        // Try load user and send hi
        if(branchState.equals(BranchState.InitState)) {
            try {
                this.userEntity = loadUserByTelegramId(message.getFrom().getId());
                return greetRecognizedUser(message);
            } catch (Exception e) {
                return greetUnrecognizedUser(message);
            }
        }

        //WaitingForAuthType State
        if(branchState.equals(BranchState.WaitingForAuthType)) {
            if(getTextFrom(message).equals(authButtonText))
                return routeToAuthorization(message);
            if(getTextFrom(message).equals(regButtonText))
                return routeToRegistration(message);
        }

        //If we got here return null
        return null;
    }

    private List<Response<?>> greetRecognizedUser(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();
        //make hi message
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                recognizedInitText + userEntity.getTelegramUsername()
        );
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //route to main menu
        setNextBranch(new MainMenuBranch(this, requestService));
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
        sendMessage.setReplyMarkup(makeKeyboardMarkup(regButtonText, authButtonText));

        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //change branch state
        branchState = BranchState.WaitingForAuthType;

        return responses;
    }

    private List<Response<?>> routeToAuthorization(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Make new branch and put in responses
        this.setNextBranch(new SignInBranch(this, requestService));
        return responses;
    }

    private List<Response<?>> routeToRegistration(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Make new branch and put in responses
        this.setNextBranch(new SignUpBranch(this, requestService));
        return responses;
    }
}
