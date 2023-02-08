package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.EditUserBranch;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.InitBranch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.notFoundException.UserNotFoundException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import io.micrometer.common.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class LogoutUserBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForSubmit()
    }
    private BranchState branchState = BranchState.InitState;

    private final String initText = "Вы уверены что хотите *выйти из аккаунта*?";
    private final String submitText = "Вы успешно вышли из аккаунта";
    private final String submitButton = "Да";

    public LogoutUserBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Nullable
    @Override
    protected List<Response<?>> makeResponses(Update update) throws BranchCriticalException {
        //Get message from update
        Message message = update.getMessage();
        //Init state
        if(branchState.equals(BranchState.InitState)) {
            return greetUser(message);
        }
        //WaitingForSubmit state
        if(branchState.equals(BranchState.WaitingForSubmit)) {
            if(getTextFrom(message).equals(submitButton)){
                return logoutUser(message);
            }
        }

        return null;
    }

    private List<Response<?>> greetUser(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), initText);
        sendMessage.setParseMode(ParseMode.MARKDOWNV2); //Set markdown for bold text
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton(submitButton));
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        branchState = BranchState.WaitingForSubmit;
        return responses;
    }

    private List<Response<?>> logoutUser(Message message) throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();
        //dissociate user on server
        try {
            requestService.dissociateTelegramIdFromUser(userEntity);
        } catch (UserNotFoundException | RequestService5xxException e) {
            throw new BranchCriticalException("Internal error");
        }
        //Notify user and route to init branch
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), submitText);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        setNextBranch(new InitBranch(this, requestService));

        return responses;
    }
}
