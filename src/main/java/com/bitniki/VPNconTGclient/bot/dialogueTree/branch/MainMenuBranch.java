package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.EditUserBranch.ShowUserBranch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.FAQ.GeneralFaqBranch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches.PeerMenuBranch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SubsBranch.SubsBranch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.RequestServiceFactory;
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
    private final String initText = "Выберите что-нибудь:";
    private final String editUserButton = "Настройка профиля";
    private final String editPeersButton = "Настройка VPN";
    private final String subsBranchButton = "Оплатить подписку";
    private final String faqButton = "О проекте";

    public MainMenuBranch(Branch prevBranch, RequestServiceFactory requestService) {
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
            String text = getTextFrom(message);
            if(text.equals(editUserButton)){
                return routeToShowUserBranch();
            }
            if(text.equals(editPeersButton)) {
                return routeToPeersMenu();
            }
            if(text.equals(subsBranchButton)) {
                return routeToSubsBranch();
            }
            if(text.equals(faqButton)) {
                return routeToGeneralFaqBranch();
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
        sendMessage.setReplyMarkup(makeKeyboardMarkup(editUserButton, editPeersButton, subsBranchButton, faqButton));

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

    private List<Response<?>> routeToGeneralFaqBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new GeneralFaqBranch(this, requestService));

        return responses;
    }
}
