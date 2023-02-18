package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class PeerMenuBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForButtonChoose()
    }
    private BranchState branchState;
    private final String initText = "Настройка VPN:";
    private final String createText = "Создать новый конфиг";
    private final String editText = "Показать созданные";

    public PeerMenuBranch(Branch prevBranch, RequestService requestService) {
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
            return providePeerMenu(message);
        }

        //WaitingForButtonChoose state
        if(branchState.equals(BranchState.WaitingForButtonChoose)) {
            if(getTextFrom(message).equals(createText)) {
                return routeToCreateBranch();
            }
            if(getTextFrom(message).equals(editText)) {
                return routeToEditPeersBranch();
            }
        }

        //If we got here return null
        return null;
    }

    private List<Response<?>> providePeerMenu(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                initText
        );
        //set nav buttons
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton(createText, editText));
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Change branch state
        branchState = BranchState.WaitingForButtonChoose;

        return  responses;
    }

    private List<Response<?>> routeToCreateBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new CreatePeerBranch(this, requestService));

        return responses;
    }

    private List<Response<?>> routeToEditPeersBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new EditPeersBranch(this, requestService));

        return responses;
    }
}
