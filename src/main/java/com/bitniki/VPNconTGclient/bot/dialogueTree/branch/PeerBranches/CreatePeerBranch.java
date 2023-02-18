package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.notFoundException.EntityNotFoundException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.exception.validationFailedException.EntityValidationFailedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.HostEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.PeerEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "TextBlockMigration"})
public class CreatePeerBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForHostChoose(),
        WaitingForPeerIp(),
        WaitingForConfName()
    }
    private BranchState branchState;

    private PeerEntity peerEntity;

    private final String hostListText = "Выбери локацию сервера:";
    private final String availablePeersText ="/254 доступно";
    private final String askPeerIpText = "Выберем тебе айпишник:" +
            "\nНапиши число 0, если тебе неважно." +
            "\nНапиши число от 2 до 254 и мы проверим доступность.";
    private final String wrongOctetText = "Число не 0 и не в промежутке от 2 до 254";
    private final String askConfNameText = "Отлично!\nА теперь придумай название конфигу.\nПодойдёт имя состоящие из латиницы и/или цифр";
    private final String showConfText = "Создал! Вот он:\n";

    public CreatePeerBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
        this.branchState = BranchState.InitState;
    }

    @Override
    protected List<Response<?>> makeResponses(Update update)
            throws BranchCriticalException {
        //Get message from update
        Message message = update.getMessage();

        //Init state
        if(branchState.equals(BranchState.InitState)) {
            return provideHostList(message);
        }

        //WaitingForHostChoose state
        if(branchState.equals(BranchState.WaitingForHostChoose)) {
            return askPeerIp(message);
        }

        //WaitingForPeerIp State
        if(branchState.equals(BranchState.WaitingForPeerIp)) {
            return askConfName(message);
        }

        //WaitingForConfName State
        if(branchState.equals(BranchState.WaitingForConfName)) {
            return createPeerAndRouteToPeerMenu(message);
        }
        //if we got here
        return null;
    }

    private List<Response<?>> provideHostList(Message message)
            throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                hostListText
        );

        //Get host list
        List<HostEntity> hostList ;
        try {
            hostList = requestService.getHostsFromServer();
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException(e.getMessage());
        }
        String[] hostListNames = hostList
                .stream()
                .map(host -> host.getName() + "\n" + host.getAvailablePeersCount() + availablePeersText)
                .toList()
                .toArray(new String[0]);
        //set nav buttons
        sendMessage.setReplyMarkup(
                makeKeyboardMarkupWithMainButton(hostListNames)
        );
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Change branch state
        branchState = BranchState.WaitingForHostChoose;
        return responses;
    }
    private List<Response<?>> askPeerIp(Message message)
            throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Validate host name and get host entity
        String hostName = getTextFrom(message).substring(
                0,
                getTextFrom(message).indexOf('\n') // Cut available peers text
                );
        //load host list and find host with given hostName or throw exception
        HostEntity hostEntity;
        try {
            hostEntity = requestService.getHostsFromServer().stream()
                    .filter(host -> host.getName().equals(hostName)).findFirst()
                    .orElseThrow(() -> new BranchBadUpdateProvidedException("No such host"));
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException(e.getMessage());
        }

        //init peerEntity and set host
        peerEntity = new PeerEntity();
        peerEntity.setHost(hostEntity);

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), askPeerIpText);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //change state
        branchState = BranchState.WaitingForPeerIp;
        return responses;
    }

    private List<Response<?>> askConfName(Message message) throws BranchBadUpdateProvidedException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Get peerIp from message and validate
        int lastOctet;
        try {
            lastOctet = Integer.parseInt(getTextFrom(message));
        } catch (NumberFormatException e) {
            throw new BranchBadUpdateProvidedException("This is not a number");
        }
        if(lastOctet == 0) {
            //peerIp will be generated on server
            peerEntity.setPeerIp(null);
        } else if (lastOctet >= 2 && lastOctet <= 254) {
            //Build peerIp and set to peerEntity
            peerEntity.setPeerIp("10.8.0." + lastOctet);
        } else {
            throw new BranchBadUpdateProvidedException(wrongOctetText);
        }

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), askConfNameText);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //change state
        branchState = BranchState.WaitingForConfName;
        return responses;
    }

    private List<Response<?>> createPeerAndRouteToPeerMenu(Message message)
            throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Set conf name to peerEntity
        peerEntity.setPeerConfName(getTextFrom(message));

        //try to create peer on server
        try {
            peerEntity = requestService.createPeerOnServer(
                    peerEntity,
                    userEntity.getId(), //User ID
                    peerEntity.getHost().getId() //Host ID
            );
        } catch (EntityValidationFailedException | EntityNotFoundException e) {
            throw new EntityValidationFailedException("Нашёл ошибку:\n" +
                                                        e.getMessage() +
                                                        "\nПопробуй ещё раз");
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException("Server fails");
        }

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), showConfText);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //Provide conf file
        SendDocument sendDocument;
        try {
            sendDocument = new SendDocument(message.getChatId().toString(), requestService.getFileFromServer(peerEntity));
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException("Server fails");
        }
        sendDocument.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendDoc, sendDocument));

        //Route to peer menu
        setNextBranch(new PeerMenuBranch(this, requestService));

        return responses;
    }
}
