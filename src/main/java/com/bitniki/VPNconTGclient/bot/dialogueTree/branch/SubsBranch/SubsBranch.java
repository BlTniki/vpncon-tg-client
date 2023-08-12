package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SubsBranch;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.Subscription;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.UserSubscription;
import com.bitniki.VPNconTGclient.bot.request.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.request.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.request.exception.requestHandler.RequestHandler5xxException;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
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
//    private final String subsText = "*!!!К сожалению, возникли проблемы с [QIWI](https://incrussia.ru/news/qiwi-ogranichila-vyvod-sredstv/)!!!*\n\n Поэтому для оплаты подписки прошу писать мне напрямую: @BITniki";
    private final String subsCardText = "*Подписка*\n%s\n\nОплата по [ссылке](%s)";
    public SubsBranch(Branch prevBranch, RequestServiceFactory requestService) {
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
            try {
                return showSubs(
                        message,
                        requestService.SUBS_REQUEST_SERVICE.getUserSubscriptionByUserId(userEntity.getId())
                );
            } catch (ModelNotFoundException e) {
                return provideSubsList(message);
            }
        }

        return null;
    }

    private List<Response<?>> provideSubsList(Message message) throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        // show user sub if exist
        try {
            responses.addAll(showSubs(
                    message,
                    requestService.SUBS_REQUEST_SERVICE.getUserSubscriptionByUserId(userEntity.getId())
            ));
        } catch (ModelNotFoundException e) {
            // do none
        }

        String chatId = message.getChatId().toString();

        //Greet user
        SendMessage sendMessage = new SendMessage(chatId, subsText);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Load subs by user role
        List<Subscription> subscriptions;
        try {
            subscriptions = requestService.SUBS_REQUEST_SERVICE.getSubsByRole(userEntity.getRole());
        } catch (RequestHandler5xxException e) {
            throw new BranchCriticalException("Critical error occurred");
        }

        //Make subs cards
        for (Subscription subscription: subscriptions) {
            //make pay url
            String payUrl;
            try {
                 payUrl = requestService.PAYMENT_REQUEST_SERVICE.makePaymentUrl(
                         userEntity.getId(),
                         subscription.getId()
                );
            } catch (ModelNotFoundException e) {
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

    private List<Response<?>> showSubs(Message message, UserSubscription userSubscription) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();
        String chatId = message.getChatId().toString();
        //Greet user
        SendMessage sendMessage = new SendMessage(
                chatId,
                String.format(
                        showSubsText,
                        userSubscription.getSubscription().describe(),
                        userSubscription.getExpirationDay().toString()
                )
        );
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        return responses;
    }
}
