package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.EditUserBranch;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.UserSubscription;
import com.bitniki.VPNconTGclient.bot.request.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.request.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class ShowUserBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForButtonChoose()
    }
    private BranchState branchState = BranchState.InitState;

    private final String initText = "Твой аккаунт:\n%s\n\nТвоя подписка:\n%s\nДень окончания подписки:\n%s";

    private final String editUserButton = "Изменить данные";
    private final String enterCodeButton = "Ввести промокод";
    private final String logoutButton = "Выйти из аккаунта";

    public ShowUserBranch(Branch prevBranch, RequestServiceFactory requestService) {
        super(prevBranch, requestService);
    }

    @Nullable
    @Override
    protected List<Response<?>> makeResponses(Update update) throws BranchCriticalException {
        //Get message from update
        Message message = update.getMessage();
        //Init state
        if(branchState.equals(BranchState.InitState)) {
           return provideEditButtons(message);
        }
        //WaitingForButtonChoose state
        if(branchState.equals(BranchState.WaitingForButtonChoose)) {
            String text = getTextFrom(message);
            if(text.equals(editUserButton)) {
                return routeToEditUserBranch();
            }
            if(text.equals(enterCodeButton)) {
                return routeToCodeBranch();
            }
            if(text.equals(logoutButton)) {
                return routeToLogoutBranch();
            }
        }
        //If we got here return null
        return null;
    }

    private List<Response<?>> provideEditButtons(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        // load user subscription
        SendMessage sendMessage;
        try {
            UserSubscription userSubscription = requestService.SUBS_REQUEST_SERVICE
                    .getUserSubscriptionByUserId(userEntity.getId());
            // on success make message
            sendMessage = new SendMessage(message.getChatId().toString(),
                    String.format(initText, userEntity,
                            userSubscription.getSubscription().describe(),
                            userSubscription.getExpirationDay()
                    )
            );
        } catch (ModelNotFoundException e) {
            sendMessage = new SendMessage(message.getChatId().toString(),
                    String.format(initText, userEntity,
                            "None",
                            "None"
                    )
            );
        }

        //set nav buttons
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton(
                editUserButton,
                enterCodeButton,
                logoutButton
                )
        );

        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //Change branch state
        branchState = BranchState.WaitingForButtonChoose;
        return  responses;
    }

    private List<Response<?>> routeToEditUserBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new EditUserBranch(this, requestService));

        return responses;
    }

    private List<Response<?>> routeToCodeBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new CodeBranch(this, requestService));

        return responses;
    }

    private List<Response<?>> routeToLogoutBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new LogoutUserBranch(this, requestService));

        return responses;
    }
}
