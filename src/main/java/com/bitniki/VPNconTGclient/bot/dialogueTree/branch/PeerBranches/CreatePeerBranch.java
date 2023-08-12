package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.validationFailedException.EntityValidationFailedException;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.Host;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.Peer;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.PeerValidatorRegex;
import com.bitniki.VPNconTGclient.bot.request.ModelForRequest.impl.PeerForRequest;
import com.bitniki.VPNconTGclient.bot.request.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.request.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.request.exception.ModelValidationFailedException;
import com.bitniki.VPNconTGclient.bot.request.exception.requestHandler.RequestHandler5xxException;
import com.bitniki.VPNconTGclient.bot.request.exception.requestHandler.RequestHandlerException;
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

    private Peer peer;
    private PeerForRequest peerForRequest;
    private final PeerValidatorRegex validator;
    private final String hostListText = "Выбери локацию сервера:";
    private final String availablePeersText ="/254 доступно";
    private final String askPeerIpText = "Выберем тебе айпишник:" +
            "\nНапиши число 0, если тебе неважно." +
            "\nНапиши число от 2 до 254 и мы проверим доступность.";
    private final String wrongOctetText = "Число не 0 и не в промежутке от 2 до 254";
    private final String peerIpAreNotAvailableText = "Этот IP занят(\nВыбери другое число или напиши 0 и подберём тебе свободный";
    private final String askConfNameText = "Отлично!\nА теперь придумай название конфигу.\nПодойдёт имя состоящие из латиницы и/или цифр";
    private final String showConfText = "Создал! Вот он:\n";

    public CreatePeerBranch(Branch prevBranch, RequestServiceFactory requestService) {
        super(prevBranch, requestService);
        this.branchState = BranchState.InitState;
        this.validator = requestService.PEER_REQUEST_SERVICE.getValidatorFields();
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
        List<Host> hostList ;
        try {
            hostList = requestService.HOST_REQUEST_SERVICE.getHostsFromServer();
        } catch (RequestHandlerException e) {
            throw new BranchCriticalException(e.getMessage());
        }
        String[] hostListNames = hostList
                .stream()
                .map(host -> {
                    Integer availablePeersCount;
                    try {
                        availablePeersCount = requestService.HOST_REQUEST_SERVICE.countAvailablePeersOnHost(host.getId());
                    } catch (ModelNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    return host.getName() + "\n" + availablePeersCount + availablePeersText;
                })
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

        //Extrude host name
        String hostName = getTextFrom(message).substring(
                0,
                getTextFrom(message).indexOf('\n') // Cut available peers text
        );

        //load host list and find host with given hostName or throw exception
        Host hostEntity;
        try {
            hostEntity = requestService.HOST_REQUEST_SERVICE.getHostsFromServer().stream()
                    .filter(host -> host.getName().equals(hostName)).findFirst()
                    .orElseThrow(() -> new BranchBadUpdateProvidedException("No such host"));
        } catch (RequestHandler5xxException e) {
            throw new BranchCriticalException(e.getMessage());
        }

        //init peerEntity and set host
        peer = new Peer();
        peerForRequest = PeerForRequest.builder()
                .hostId(hostEntity.getId())
                .userId(userEntity.getId())
                .build();
        peer.setHost(hostEntity);
        peer.setUser(this.userEntity);

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
            peer.setPeerIp(null);
        } else if (lastOctet >= 2 && lastOctet <= 254) {
            //Build peerIp and set to peerEntity
            String hostSubNetPrefix = peer.getHost().getHostInternalNetworkPrefix();
            hostSubNetPrefix = hostSubNetPrefix.substring(0, hostSubNetPrefix.length() - 1);//cut last octet;
            String peerIp = hostSubNetPrefix + lastOctet;

            // set to entity
            peer.setPeerIp(peerIp);
            peerForRequest.setPeerIp(peerIp);

            // check for peerIp uniques
            if (requestService.PEER_REQUEST_SERVICE.isPeerIpAlreadyExistOnHost(peerIp, peerForRequest.getHostId())) {
                throw new BranchBadUpdateProvidedException(peerIpAreNotAvailableText);
            }
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

        // extrude peerConfName
        String peerConfName = getTextFrom(message);

        // validate peer conf name
        if (validator.isPeerConfNameNotValid(peerConfName)) {
            throw new BranchBadUpdateProvidedException("Название конфига некорректно, давай ещё раз");
        }

        //Set conf name to peerEntity
        peer.setPeerConfName(getTextFrom(message));
        peerForRequest.setPeerConfName(getTextFrom(message));

        //try to create peer on server
        try {
            peer = requestService.PEER_REQUEST_SERVICE.createPeerOnServer(
                    peerForRequest
            );
        } catch (ModelNotFoundException | ModelValidationFailedException e) {
            throw new EntityValidationFailedException("Нашёл ошибку:\n" +
                    e.getMessage() +
                    "\nПопробуй ещё раз");
        } catch (RequestHandlerException e) {
            throw new BranchCriticalException("Server fails");
        }

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), showConfText);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Provide conf file
        SendDocument sendDocument;
        try {
            sendDocument = new SendDocument(
                    message.getChatId().toString(),
                    requestService.PEER_REQUEST_SERVICE.getConfigFileFromServer(peer)
            );
        } catch (RequestHandlerException | ModelNotFoundException e) {
            throw new BranchCriticalException("Server fails");
        }
        sendDocument.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendDoc, sendDocument));

        //Route to peer menu
        setNextBranch(new PeerMenuBranch(this, requestService));

        return responses;
    }
}
