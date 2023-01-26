package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestServiceException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public abstract class Branch {
    private Branch prevBranch;
    private Branch nextBranch;
    private boolean isBranchWantChangeBranch;
    protected RequestService requestService;

    private final String errorText = "Что-то я не смог тебя понять. Попробуй ещё раз. " +
            "Ну или напиши мне: @BITniki";
    protected final String returnText = "Главная";

    public Branch(RequestService requestService) {
        this.requestService = requestService;
    }

    public Branch(Branch prevBranch, RequestService requestService) {
        this.prevBranch = prevBranch;
        this.requestService = requestService;
    }

    public List<Response<?>> handle(Update update)
            throws RequestServiceException, BranchBadUpdateProvidedException{
        List<Response<?>> responses = makeResponses(update);
        if(responses == null) throw new BranchBadUpdateProvidedException(errorText);
        return responses;
    }
    protected abstract List<Response<?>> makeResponses(Update update)
            throws RequestServiceException, BranchBadUpdateProvidedException;

    public Branch getPrevBranch() {
        return prevBranch;
    }

    public void setPrevBranch(Branch prevBranch) {
        this.prevBranch = prevBranch;
    }

    public Branch getNextBranch() {
        return nextBranch;
    }

    public void setNextBranch(Branch nextBranch) {
        this.nextBranch = nextBranch;
        this.isBranchWantChangeBranch = true;
    }

    public boolean isBranchWantChangeBranch() {
        return isBranchWantChangeBranch;
    }

    public RequestService getRequestService() {
        return requestService;
    }

    public void setRequestService(RequestService requestService) {
        this.requestService = requestService;
    }

    public ReplyKeyboardMarkup makeKeyboardMarkup(String... buttons) {
        KeyboardRow keyboardRow = new KeyboardRow();
        for (String button: buttons) {
            keyboardRow.add(new KeyboardButton(button));
        }
        return new ReplyKeyboardMarkup(List.of(keyboardRow));
    }

    public ReplyKeyboardMarkup makeKeyboardMarkupWithMainButton(String... buttons) {
        KeyboardRow keyboardRow = new KeyboardRow();
        for (String button: buttons) {
            keyboardRow.add(new KeyboardButton(button));
        }
        KeyboardRow mainKeyboardRow = new KeyboardRow();
        mainKeyboardRow.add(returnText);
        return new ReplyKeyboardMarkup(List.of(keyboardRow, mainKeyboardRow));
    }
}
