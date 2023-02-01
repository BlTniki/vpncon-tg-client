package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.response.Response;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public abstract class AuthBranch extends Branch{
    private final String returnText = "Назад";
    private final String errorText = "Что-то я не смог тебя понять. Попробуй ещё раз. " +
            "Ну или напиши мне: @BITniki";
    public AuthBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Override
    public List<Response<?>> handle(Update update) throws BranchCriticalException {
        //Try to make responses
        List<Response<?>> responses = makeResponses(update);
        //If returnToMainMenu button were pressed
        if(update.getMessage().hasText()
                && update.getMessage().getText().equals(returnText)) {
            //route to main menu chain
            this.setNextBranch(new InitBranch(this, requestService));
            //clear message text or it will fall in endless cycle
            update.getMessage().setText("");
            //return empty list
            return new ArrayList<>();
        }
        //makeResponses can return null, in this case — throw exception
        if(responses == null) throw new BranchBadUpdateProvidedException(this.errorText);
        return responses;
    }

    @Override
    protected ReplyKeyboardMarkup makeKeyboardMarkupWithMainButton(String... buttons) {
        //change return text
        KeyboardRow keyboardRow = new KeyboardRow();
        for (String button: buttons) {
            keyboardRow.add(new KeyboardButton(button));
        }
        KeyboardRow mainKeyboardRow = new KeyboardRow();
        mainKeyboardRow.add(returnText);
        return new ReplyKeyboardMarkup(List.of(keyboardRow, mainKeyboardRow));
    }
}
