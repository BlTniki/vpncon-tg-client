package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.BranchWithUser;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestServiceException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.HostEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class CreatePeerBranch extends BranchWithUser {
    private enum BranchState {
        InitState(),
        WaitingForHostChoose()
    }
    private BranchState branchState;

    private final String hostListText = "Выбери локацию сервера:";

    public CreatePeerBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
        this.branchState = BranchState.InitState;
    }

    public CreatePeerBranch(BranchWithUser prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
        this.branchState = BranchState.InitState;
    }

    @Override
    protected List<Response<?>> makeResponses(Update update)
            throws RequestServiceException, BranchBadUpdateProvidedException {
        //Get message from update
        Message message = update.getMessage();

        //Init state
        if(branchState.equals(BranchState.InitState)) {
            return provideHostList(message);
        }

        return null;
    }

    private List<Response<?>> provideHostList(Message message)
            throws RequestService5xxException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                hostListText
        );

        //Get host list
        List<HostEntity> hostList = requestService.getHostsFromServer();
        String[] hostListNames = hostList
                .stream()
                .map(HostEntity::getName)
                .toList()
                .toArray(new String[0]);
        //set nav buttons
        sendMessage.setReplyMarkup(
                makeKeyboardMarkupWithMainButton(hostListNames)
        );
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        return responses;
    }
}
