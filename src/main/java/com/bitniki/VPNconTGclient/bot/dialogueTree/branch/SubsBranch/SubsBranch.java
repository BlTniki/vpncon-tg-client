package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SubsBranch;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.SubscriptionEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;

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
    private final String subsText = "Вот доступные тебе подписки";
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
        String chatId = message.getChatId().toString();
        //Greet user
        SendMessage sendMessage = new SendMessage(chatId, subsText);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //Load subs by user role
        List<SubscriptionEntity> subscriptions;
        try {
            subscriptions = requestService.getSubsByRole(userEntity.getRole());
        } catch (RequestService5xxException e) {
            throw new BranchCriticalException("Critical error occurred");
        }
        //Make invoices
        for (SubscriptionEntity subscription: subscriptions) {
            responses.add(new Response<>(
                            ResponseType.SendInvoice,
                            makeRawSubscriptionInvoice(chatId, subscription)
                    )
            );
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

    private SendInvoice makeRawSubscriptionInvoice(String chatId, SubscriptionEntity subscription) {
        LabeledPrice price = new LabeledPrice(
                "Подписка",
                subscription.getPriceInRub()*100
        );
        SendInvoice sendInvoice = new SendInvoice();
        sendInvoice.setChatId(chatId);
        sendInvoice.setTitle("Подписка");
        sendInvoice.setDescription(subscription.describe());
        sendInvoice.setPayload(subscription.toString());
        sendInvoice.setCurrency("RUB");
        sendInvoice.setPrices(List.of(price));
        return sendInvoice;
    }
}
