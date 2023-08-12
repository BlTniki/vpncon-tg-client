package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.Peer;
import com.bitniki.VPNconTGclient.bot.request.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.request.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.request.exception.requestHandler.RequestHandlerException;
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
    private Peer peerEntity;

    private final String providePeerListText = "Выбери конфиг:";
    private final String providePeerText = "Твой конфиг:\n%s";
    private final String activateButton = "Активировать";
    private final String deactivateButton = "Деактивировать";
    private final String deleteButton = "Удалить";
    private final String successText = "Успешно";

    public EditPeersBranch(Branch prevBranch, RequestServiceFactory requestService) {
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
            if(getTextFrom(message).equals(activateButton)) {
                return activatePeer(message);
            }
            if(getTextFrom(message).equals(deactivateButton)) {
                return deactivatePeer(message);
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
        String[] peerNames = requestService.PEER_REQUEST_SERVICE.getPeersByUserId(userEntity.getId())
                .stream()
                .map(Peer::getPeerConfName)
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
        peerEntity = requestService.PEER_REQUEST_SERVICE.getPeersByUserId(userEntity.getId()).stream()
                .filter(peer -> peer.getPeerConfName().equals(peerName))
                .findFirst()
                .orElseThrow(() -> new BranchBadUpdateProvidedException("There no such peer!"));

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                String.format(providePeerText, peerEntity)
        );

        //Provide buttons
        String activateDeactivateButton;
        if (peerEntity.getIsActivated()) {
            activateDeactivateButton = deactivateButton;
        } else {
            activateDeactivateButton = activateButton;
        }
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton(activateDeactivateButton, deleteButton));
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Provide file
        InputFile file;
        try {
            file = requestService.PEER_REQUEST_SERVICE.getConfigFileFromServer(peerEntity);
        } catch (RequestHandlerException | ModelNotFoundException e) {
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
            requestService.PEER_REQUEST_SERVICE.deletePeerOnServer(peerEntity.getId());
        } catch (RequestHandlerException | ModelNotFoundException e) {
            throw new BranchCriticalException("Some real problems");
        }

        // Make response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), successText);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //Route to EditPeerBranch
        setNextBranch(new EditPeersBranch(this, requestService));

        return responses;
    }

    private List<Response<?>> activatePeer(Message message) throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        try {
            requestService.PEER_REQUEST_SERVICE.activatePeer(peerEntity.getId());
        } catch (RequestHandlerException | ModelNotFoundException e) {
            throw new BranchCriticalException("Some real problems");
        }

        // Make response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), successText);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //Route to EditPeerBranch
        setNextBranch(new EditPeersBranch(this, requestService));

        return responses;
    }

    private List<Response<?>> deactivatePeer(Message message) throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        try {
            requestService.PEER_REQUEST_SERVICE.deactivatePeer(peerEntity.getId());
        } catch (RequestHandlerException | ModelNotFoundException e) {
            throw new BranchCriticalException("Some real problems");
        }

        // Make response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), successText);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //Route to EditPeerBranch
        setNextBranch(new EditPeersBranch(this, requestService));

        return responses;
    }
}
