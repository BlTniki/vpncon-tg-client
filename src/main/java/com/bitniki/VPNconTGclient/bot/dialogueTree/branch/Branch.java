package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.notFoundException.UserNotFoundException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestServiceException;
import com.bitniki.VPNconTGclient.bot.exception.validationFailedException.UserValidationFailedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
            throws BranchCriticalException{
        //If returnToMainMenu button were pressed
        if(
            update.getMessage().hasText()
                && update.getMessage().getText().equals(returnText)
        ) {
            //route to main menu chain
            this.setNextBranch(new MainMenuBranch(this, requestService));
            //clear message text or it will fall in endless cycle
            update.getMessage().setText("");
            //return empty list
            return new ArrayList<>();
        }
        //Try to make responses
        List<Response<?>> responses = makeResponses(update);
        //makeResponses can return null, in this case — throw exception
        if(responses == null) throw new BranchBadUpdateProvidedException(errorText);
        return responses;
    }

    protected abstract @Nullable List<Response<?>> makeResponses(Update update)
            throws BranchCriticalException;

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

    /**
     * Save way to get text from message
     * @param message — Message object
     * @return Text from message
     * @throws BranchBadUpdateProvidedException if there is no text
     */
    protected String getTextFrom(Message message) throws BranchBadUpdateProvidedException {
        if(message.hasText()) return message.getText();
        else throw new BranchBadUpdateProvidedException("there no text!");
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

    public UserEntity loadUserByTelegramId(Long telegramId)
            throws RequestServiceException, UserNotFoundException, UserValidationFailedException {
        return requestService.getUserByTelegramId(telegramId);
    }
}
