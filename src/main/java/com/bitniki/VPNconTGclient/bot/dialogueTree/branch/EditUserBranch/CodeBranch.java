package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.EditUserBranch;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.notFoundException.EntityNotFoundException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import io.micrometer.common.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class CodeBranch extends Branch{
    private enum BranchState {
        InitState(),
        WaitingForCode()
    }
    private BranchState branchState = BranchState.InitState;

    private final String initText = "Введи промокод:";
    private final String errorText = "Неверный промокод, попробуй ещё раз";
    private final String successText = "Успех!";

    public CodeBranch(Branch prevBranch, RequestService requestService) {
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
        //WaitingForCode state
        if(branchState.equals(BranchState.WaitingForCode)) {
            return useCode(message);
        }

        return null;
    }

    private List<Response<?>> greetUser(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), initText);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        branchState = BranchState.WaitingForCode;
        return responses;
    }

    private List<Response<?>> useCode(Message message) throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Use code on server
        String token = getTextFrom(message);
        try {
            requestService.useCodeOnServer(userEntity.getId().toString(), token);
        } catch (EntityNotFoundException e) {
            throw new BranchBadUpdateProvidedException(errorText);
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException("internal error");
        }

        //respond
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), successText);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        return responses;
    }
}
