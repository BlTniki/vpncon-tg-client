package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.exception.RequestServiceException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class MainMenuBranch extends BranchWithUser{
    private enum BranchState {
        InitState(),
        WaitingForButtonChoose()
    }
    private BranchState branchState;
    private final String initText = "Выбери ченибуть:";
    private final String errorText = "Похоже на внутренюю ошибку бота. " +
            "Напиши мне: @BITniki";
    private final String editUserText = "Настрока профиля";
    private final String editPeersText = "Настройка VPN";

    public MainMenuBranch(BranchWithUser prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
        this.branchState = BranchState.InitState;
    }

    @Override
    public List<Response<?>> handle(Update update) throws RequestServiceException, BranchBadUpdateProvidedException {
        //Get message from update
        Message message = update.getMessage();

        //Init state
        if(branchState.equals(BranchState.InitState)) {
           return provideMainMenuButtons(message);
        }

        //WaitingForButtonChoose State
        if(branchState.equals(BranchState.WaitingForButtonChoose)) {
            if(message.getText().equals(editUserText)){

            }
            if(message.getText().equals(editPeersText)){
                return routeToPeersMenu(message);
            }
        }

        //If we got here send error
        throw new BranchBadUpdateProvidedException(
                errorText
        );
    }

    private List<Response<?>> provideMainMenuButtons(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                initText
        );

        //set nav buttons
        KeyboardRow keyboardRow = new KeyboardRow(List.of(
                new KeyboardButton(editUserText),
                new KeyboardButton(editPeersText)
        ));
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardRow));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Change branch state
        branchState = BranchState.WaitingForButtonChoose;

        return  responses;
    }

    private List<Response<?>> routeToPeersMenu(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new PeerMenuBranch(this, requestService));

        return responses;
    }
}
