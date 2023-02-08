package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.EditUserBranch.ShowUserBranch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches.PeerMenuBranch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SubsBranch.SubsBranch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class MainMenuBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForButtonChoose()
    }
    private BranchState branchState;
    private final String initText = "Выбери ченибуть:";
    private final String editUserText = "Настрока профиля";
    private final String editPeersText = "Настройка VPN";
    private final String subsBranchText = "Оплатить подписку";

    public MainMenuBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
        this.branchState = BranchState.InitState;
    }

    @Override
    public List<Response<?>> makeResponses(Update update)
            throws BranchBadUpdateProvidedException {
        //Get message from update
        Message message = update.getMessage();

        //Init state
        if(branchState.equals(BranchState.InitState)) {
           return provideMainMenuButtons(message);
        }

        //WaitingForButtonChoose State
        if(branchState.equals(BranchState.WaitingForButtonChoose)) {
            if(message.getText().equals(editUserText)){
                return routeToShowUserBranch();
            }
            if(getTextFrom(message).equals(editPeersText)) {
                return routeToPeersMenu();
            }
            if(getTextFrom(message).equals(subsBranchText)) {
                return routeToSubsBranch();
            }
        }

        //If we got here return null
        return null;
    }

    private List<Response<?>> provideMainMenuButtons(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                initText
        );

        //set nav buttons
        sendMessage.setReplyMarkup(makeKeyboardMarkup(editUserText, editPeersText, subsBranchText));

        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Change branch state
        branchState = BranchState.WaitingForButtonChoose;

        return  responses;
    }

    private List<Response<?>> routeToShowUserBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new ShowUserBranch(this, requestService));

        return responses;
    }

    private List<Response<?>> routeToPeersMenu() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new PeerMenuBranch(this, requestService));

        return responses;
    }

    private List<Response<?>> routeToSubsBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new SubsBranch(this, requestService));

        return responses;
    }
}
