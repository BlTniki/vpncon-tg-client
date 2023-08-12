package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.request.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.response.Response;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public abstract class AuthBranch extends Branch{
    private final String returnText = "Главная";
    private final String errorText = "Что-то я не смог тебя понять. Попробуй ещё раз. " +
            "Ну или напиши мне: @BITniki";
    public AuthBranch(Branch prevBranch, RequestServiceFactory requestService) {
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
}
