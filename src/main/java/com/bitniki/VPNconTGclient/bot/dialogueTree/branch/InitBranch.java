package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.exception.BranchBadUpdateProvidedException;
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

    private final String errorText = "Похоже на внутренюю ошибку бота. " +
                                     "Напиши мне: @BITniki";
    private final String regButtonText = "Регистрация";
    private final String authButtonText = "Авторизация";

    public InitBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
        this.branchState = BranchState.InitState;
    }

    @Override
    public List<Response<?>> handle(Update update)
            throws BranchBadUpdateProvidedException {
        //Get message from update
        Message message = update.getMessage();

        // Try load user and send hi
        if(branchState.equals(BranchState.InitState)) {
            try {
                this.userEntity = loadUserByTelegramId(message.getFrom().getId());
                return greetRecognizedUser(message);
            } catch (Exception e) {
                return greetUnrecognizedUser(message);
            }
        }

        if(branchState.equals(BranchState.WaitingForAuthType)) {
            if(message.getText().equals(authButtonText))
                return routeToAuthorization(message);
            if(message.getText().equals(regButtonText))
                return routeToRegistration(message);
        }

        //If we got here send error
        //if we got here send error
        throw new BranchBadUpdateProvidedException(
                errorText
        );
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
                new KeyboardButton(regButtonText),
                new KeyboardButton(authButtonText)
        ));
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardRow));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));

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
