package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SubsBranch;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.SubscriptionEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class SubsBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForPayment(),
    }
    private BranchState branchState = BranchState.InitState;
    private final String showSubsText = "Вот твоя подписка:\n%s\nДата окончания подписки: %s";
    private final String subsText = "Вот доступные тебе подписки.\nПеред совершением оплаты, пожалуйста, ознакомтесь с разделом '*О проекте*'.";
    private final String subsCardText = "*Подписка*\n%s\n\nОплата по [ссылке](%s)";
    public SubsBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Nullable
    @Override
    protected List<Response<?>> makeResponses(Update update) throws BranchCriticalException {

        //Get message from update
        Message message = update.getMessage();

        if(branchState.equals(BranchState.InitState)) {
            return provideSubsList(message);
        }
        if(branchState.equals(BranchState.WaitingForPayment)) {
            return showSubs(message);
        }
        return null;
    }

    private List<Response<?>> provideSubsList(Message message) throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        if(userEntity.getSubscription() != null) {
            responses.addAll(showSubs(message));
        }

        String chatId = message.getChatId().toString();
        //Greet user
        SendMessage sendMessage = new SendMessage(chatId, subsText);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //Load subs by user role
        List<SubscriptionEntity> subscriptions;
        try {
            subscriptions = requestService.getSubsByRole(userEntity.getRole());
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException("Critical error occurred");
        }
        //Make subs cards
        for (SubscriptionEntity subscription: subscriptions) {
            //make pay url
            String payUrl;
            try {
                 payUrl = requestService.makePaymentUrl(
                        subscription.getId(),
                        userEntity.getId()
                );
            } catch (URISyntaxException e) {
                continue;
            }
            SendMessage subsCard = new SendMessage(
                    chatId,
                    String.format(subsCardText, subscription.describe(), payUrl)
                    );
            subsCard.setParseMode(ParseMode.MARKDOWN);
            responses.add(new Response<>(ResponseType.SendText, subsCard));

        }
        //Change state
        branchState = BranchState.WaitingForPayment;
        return responses;
    }

    private List<Response<?>> showSubs(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();
        String chatId = message.getChatId().toString();
        //Greet user
        SendMessage sendMessage = new SendMessage(
                chatId,
                String.format(
                        showSubsText,
                        userEntity.getSubscription().describe(),
                        userEntity.getSubscriptionExpirationDay().toString()
                )
        );
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        return responses;
    }
}
