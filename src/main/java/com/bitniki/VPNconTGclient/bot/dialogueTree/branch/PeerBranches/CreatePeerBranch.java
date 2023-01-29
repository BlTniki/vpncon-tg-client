package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.BranchWithUser;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestServiceException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.HostEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.PeerEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "TextBlockMigration"})
public class CreatePeerBranch extends BranchWithUser {
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
    private final String askPeerIpText = "Веберем тебе айпишник:" +
            "\nНапиши число 0, если тебе неважно." +
            "\nНапиши число от 2 до 254 и мы проверим доступность.";
    private final String wrongOctetText = "Число не 0 и не в промежутке от 2 до 254";
    private final String askConfNameText = "Отлично!\nА теперь придумай название конфигу.\nПодойдёт имя состоящие из латиницы и цифр";

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

        //WaitingForHostChoose state
        if(branchState.equals(BranchState.WaitingForHostChoose)) {
            return askPeerIp(message);
        }

        //WaitingForPeerIp State
        if(branchState.equals(BranchState.WaitingForPeerIp)) {
            return askConfName(message);
        }

        //WaitingForConfName State

        //if we got here
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
            throws RequestService5xxException, BranchBadUpdateProvidedException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Validate host name and get host entity
        String hostName = getTextFrom(message).substring(
                0,
                getTextFrom(message).indexOf('\n') // Cut available peers text
                );
        //load host list and find host with given hostName or throw exception
        HostEntity hostEntity = requestService.getHostsFromServer().stream()
                .filter(host -> host.getName().equals(hostName)).findFirst()
                .orElseThrow(() -> new BranchBadUpdateProvidedException("No such host"));

        //init peerEntity and set host
        peerEntity = new PeerEntity();
        peerEntity.setHost(hostEntity);

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), askPeerIpText);
        //Force reply
        sendMessage.setReplyMarkup(new ForceReplyKeyboard());
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
        //Force reply
        sendMessage.setReplyMarkup(new ForceReplyKeyboard());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //change state
        branchState = BranchState.WaitingForConfName;
        return responses;
    }
}
