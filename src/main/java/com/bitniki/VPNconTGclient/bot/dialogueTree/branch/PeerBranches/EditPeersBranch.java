package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.notFoundException.EntityNotFoundException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.PeerEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
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
        WaitingForEditChose()
    }
    private BranchState branchState = BranchState.InitState;
    private PeerEntity peerEntity;

    private final String providePeerListText = "Выбери конфиг:";
    private final String providePeerText = "Твой конфиг:\n%s\nВыбери, что будешь менять";
    private final String editIpButton = "Изменить peerIp";
    private final String deleteButton = "Удалить";
    private final String deletePeerText = "Успешно";

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
        if(branchState.equals(BranchState.WaitingForPeerChoose)) {
            return providePeer(message);
        }
        if(branchState.equals(BranchState.WaitingForEditChose)) {
            if(getTextFrom(message).equals(deleteButton)) {
                return deletePeer(message);
            }
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

    private List<Response<?>> providePeer(Message message) throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Get peer by name or throw exception
        String peerName = getTextFrom(message);
        peerEntity = userEntity.getPeers().stream()
                .filter(peer -> peer.getPeerConfName().equals(peerName))
                .findFirst()
                .orElseThrow(() -> new BranchBadUpdateProvidedException("There no such peer!"));

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                String.format(providePeerText, peerEntity)
        );

        //Provide buttons
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton(editIpButton, deleteButton));
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Provide file
        InputFile file;
        try {
            file = requestService.getFileFromServer(peerEntity);
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException("Ouch");
        }
        SendDocument sendDocument = new SendDocument(message.getChatId().toString(), file);
        responses.add(new Response<>(ResponseType.SendDoc, sendDocument));

        //Change state
        branchState = BranchState.WaitingForEditChose;
        return responses;
    }

    private List<Response<?>> deletePeer(Message message) throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        try {
            requestService.deletePeerOnServer(peerEntity);
        } catch (EntityNotFoundException | RequestService5xxException e) {
            throw new BranchCriticalException("Some real problems");
        }
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), deletePeerText);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //Route to EditPeerBranch
        setNextBranch(new EditPeersBranch(this, requestService));

        return responses;
    }
}
