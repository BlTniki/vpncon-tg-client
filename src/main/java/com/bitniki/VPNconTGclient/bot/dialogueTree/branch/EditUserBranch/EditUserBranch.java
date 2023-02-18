package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.EditUserBranch;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.InitBranch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.exception.validationFailedException.UserValidationFailedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class EditUserBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForButtonChoose(),
        WaitingForNewLogin(),
        WaitingForNewPassword()
    }
    private BranchState branchState = BranchState.InitState;

    private final String initText = "Твой аккаунт:\n%s";
    private final String editLoginText = "Введи новый логин";
    private final String editPasswordText = "Введи новый пароль";
    private final String editSuccessText = "Успех!";
    private final String editErrorText = "Нашёл ошибку:\n%s\nПопробуй ещё раз";
    private final String editLoginButton = "Изменить логин";
    private final String editPasswordButton = "Изменить пароль";
    private final String deleteButton = "Удалить аккаунт";

    public EditUserBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Nullable
    @Override
    protected List<Response<?>> makeResponses(Update update) throws BranchCriticalException {
        //Get message from update
        Message message = update.getMessage();
        //Init state
        if(branchState.equals(BranchState.InitState)) {
            return provideEditButtons(message);
        }
        if(branchState.equals(BranchState.WaitingForButtonChoose)) {
            String text = getTextFrom(message);
            if(text.equals(editLoginButton)) {
                return provideEditLogin(message);
            }
            if(text.equals(editPasswordButton)) {
                return provideEditPassword(message);
            }
            if(text.equals(deleteButton)) {
                return routeToDeleteUserBranch();
            }
        }
        if(branchState.equals(BranchState.WaitingForNewLogin)) {
            return changeLoginOnServer(message);
        }
        if(branchState.equals(BranchState.WaitingForNewPassword)) {
            return changePasswordOnServer(message);
        }

        return null;
    }

    private List<Response<?>> provideEditButtons(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                String.format(initText, userEntity)
        );

        //set nav buttons
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton(editLoginButton, editPasswordButton, deleteButton));
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Change branch state
        branchState = BranchState.WaitingForButtonChoose;

        return responses;
    }

    private List<Response<?>> provideEditLogin(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                editLoginText
        );
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        branchState = BranchState.WaitingForNewLogin;

        return responses;
    }

    private List<Response<?>> provideEditPassword(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                editPasswordText
        );
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        branchState = BranchState.WaitingForNewPassword;

        return responses;
    }

    private List<Response<?>> routeToDeleteUserBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new DeleteUserBranch(this, requestService));

        return responses;
    }

    private List<Response<?>> changeLoginOnServer(Message message) throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Update login on server
        UserEntity newUser = new UserEntity(getTextFrom(message), null);
        try {
            requestService.updateUserOnServer(userEntity.getId().toString(),newUser);
        } catch (UserValidationFailedException e) {
            throw new UserValidationFailedException(String.format(editErrorText,e.getMessage()));
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException(e.getMessage());
        }

        //notify user on success and re init this branch
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), editSuccessText);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        setNextBranch(new EditUserBranch(this, requestService));
        return responses;
    }

    private List<Response<?>> changePasswordOnServer(Message message) throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Update login on server
        UserEntity newUser = new UserEntity(null, getTextFrom(message));
        try {
            requestService.updateUserOnServer(userEntity.getId().toString(),newUser);
        } catch (UserValidationFailedException e) {
            throw new UserValidationFailedException(String.format(editErrorText,e.getMessage()));
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException(e.getMessage());
        }

        //notify user on success and logout
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), editSuccessText);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        try {
            requestService.dissociateTelegramIdFromUser(userEntity);
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException(e.getMessage());
        }
        setNextBranch(new InitBranch(this, requestService));
        return responses;
    }
}
