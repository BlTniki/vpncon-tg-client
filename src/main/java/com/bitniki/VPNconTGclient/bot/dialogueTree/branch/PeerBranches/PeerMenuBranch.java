package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.BranchWithUser;
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
public class PeerMenuBranch extends BranchWithUser {
    private enum BranchState {
        InitState()
    }
    private BranchState branchState;
    private final String errorText = "Похоже на внутренюю ошибку бота. " +
            "Напиши мне: @BITniki";

    public PeerMenuBranch(BranchWithUser prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
        this.branchState = BranchState.InitState;
    }

    @Override
    public List<Response<?>> makeResponses(Update update) throws BranchBadUpdateProvidedException {
        //Get message from update
        Message message = update.getMessage();

        //Init state
        if(branchState.equals(BranchState.InitState)) {
            return returnPeersList(message);
        }

        //If we got here return null
        return null;
    }

    private List<Response<?>> returnPeersList(Message message) throws BranchBadUpdateProvidedException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        // Try load user peers
        try {
            this.userEntity = loadUserByTelegramId(message.getFrom().getId());
        } catch (Exception e) {
            throw new BranchBadUpdateProvidedException(
                    errorText
            );
        }

        //Make message
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                userEntity.getPeers().toString()
        );
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        return responses;
    }
}
