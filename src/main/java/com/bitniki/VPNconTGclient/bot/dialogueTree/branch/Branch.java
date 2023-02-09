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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public abstract class Branch {
    protected UserEntity userEntity;
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
        //Authorize user
        authorizeUser(update);
        //If returnToMainMenu button were pressed
        if(update.getMessage().hasText()
                && update.getMessage().getText().equals(returnText)) {
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

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

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
        if(message.hasText()) {
            return message.getText();
        }
        else {
            throw new BranchBadUpdateProvidedException("there no text!");
        }
    }

    protected ReplyKeyboardMarkup makeKeyboardMarkup(int elementsPerRow, @Nonnull String... buttons) {
        if (elementsPerRow <= 0) {
            throw new IllegalArgumentException("The number of elements per row must be greater than 0");
        }
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        // Make rows with the specified number of elements
        for (int i = 0; i < buttons.length; i += elementsPerRow) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (int j = i; j < Math.min(i + elementsPerRow, buttons.length); j++) {
                keyboardRow.add(new KeyboardButton(buttons[j]));
            }
            keyboardRowList.add(keyboardRow);
        }
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRowList);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    protected ReplyKeyboardMarkup makeKeyboardMarkup(@Nonnull String... buttons) {
        return makeKeyboardMarkup(3, buttons);
    }

    protected ReplyKeyboardMarkup makeKeyboardMarkupWithMainButton(int elementsPerRow, String... buttons) {
        if (elementsPerRow <= 0) {
            throw new IllegalArgumentException("The number of elements per row must be greater than 0");
        }
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        // Make rows with the specified number of elements
        for (int i = 0; i < buttons.length; i += elementsPerRow) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (int j = i; j < Math.min(i + elementsPerRow, buttons.length); j++) {
                keyboardRow.add(new KeyboardButton(buttons[j]));
            }
            keyboardRowList.add(keyboardRow);
        }
        // Add the main menu button
        KeyboardRow mainKeyboardRow = new KeyboardRow();
        mainKeyboardRow.add(returnText);
        keyboardRowList.add(mainKeyboardRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRowList);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    protected ReplyKeyboardMarkup makeKeyboardMarkupWithMainButton(String... buttons) {
        return makeKeyboardMarkupWithMainButton(3, buttons);
    }

    protected UserEntity loadUserByTelegramId(Long telegramId)
            throws RequestServiceException, UserNotFoundException, UserValidationFailedException {
        return requestService.getUserByTelegramId(telegramId);
    }

    protected void authorizeUser(Update update) throws BranchCriticalException {
        try {
            userEntity = loadUserByTelegramId(update.getMessage().getFrom().getId());
        } catch (BranchBadUpdateProvidedException | RequestServiceException e) {
            throw new BranchCriticalException("Cant authenticate");
        }
    }
}
