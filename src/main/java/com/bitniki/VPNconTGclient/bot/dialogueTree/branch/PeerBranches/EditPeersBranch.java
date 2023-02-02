package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.PeerEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class EditPeersBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForPeerChoose(),
        WaitingForPeerIp(),
        WaitingForConfName()
    }
    private BranchState branchState = BranchState.InitState;

    private final String providePeerListText = "Выбери конфиг:";

    public EditPeersBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Nullable
    @Override
    protected List<Response<?>> makeResponses(Update update) throws BranchCriticalException {
        //Get message from update
        Message message = update.getMessage();

        if(branchState.equals(BranchState.InitState)) {
            return providePeerList(message);
        }

        return null;
    }

    private List<Response<?>> providePeerList(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                providePeerListText
        );

        //Get peer names
        String[] peerNames = userEntity.getPeers()
                .stream()
                .map(PeerEntity::getPeerConfName)
                .toList().toArray(new String[0]);
        //Provide buttons
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton(peerNames));
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Change state
        branchState = BranchState.WaitingForPeerChoose;
        return responses;
    }
}
