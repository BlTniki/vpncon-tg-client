package com.bitniki.VPNconTGclient.bot.dialogueTree;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.InitBranch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.exception.validationFailedException.EntityValidationFailedException;
import com.bitniki.VPNconTGclient.bot.request.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class Tree {
    private Branch currentBranch;
    private final RequestServiceFactory requestService;
    private int badRequestCount;

    private final String criticalErrorText = "Что-то пошло не так. Давай начнём сначала\n"
            + "Напиши мне: @BITniki";

    public Tree(RequestServiceFactory requestService) {
        this.requestService = requestService;
        this.currentBranch = new InitBranch(null, requestService);
    }

    public List<Response<?>> handle(Update update) {
        // Get responses from branch
        List<Response<?>> responses = new ArrayList<>();
        try {
            responses.addAll(this.currentBranch.handle(update));
            //Reset the counter
            badRequestCount = 0;
        } catch (EntityValidationFailedException e) {
            SendMessage sendMessage = new SendMessage(
                    update.getMessage().getChatId().toString(),
                    e.getMessage()
            );
            responses.add(new Response<>(ResponseType.SendText, sendMessage));
            //Restart branch
            try {
                currentBranch.setNextBranch(
                        currentBranch.getClass().getConstructor(
                                Branch.class, RequestServiceFactory.class
                        ).newInstance(currentBranch, requestService)
                );
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        } catch (BranchBadUpdateProvidedException e) {
            SendMessage sendMessage = new SendMessage(
                    update.getMessage().getChatId().toString(),
                    e.getMessage()
            );
            responses.add(
                    new Response<>(ResponseType.SendText, sendMessage)
            );
            //Reset the tree if there are 3 failed requests in a row
            if(badRequestCount++ % 4 == 3) {
                badRequestCount = 0;
                //route to InitBranch
                this.currentBranch.setNextBranch(new InitBranch(null, requestService));
            }
        } catch (BranchCriticalException e) {
            SendMessage sendMessage = new SendMessage(
                    update.getMessage().getChatId().toString(),
                    criticalErrorText
            );
            responses.add(
                    new Response<>(ResponseType.SendText, sendMessage)
            );
            this.currentBranch.setNextBranch(new InitBranch(null, requestService));
        }

        //if branch want change branch — change
        if(this.currentBranch.isBranchWantChangeBranch()) {
            this.currentBranch = this.currentBranch.getNextBranch();
            //do init response
            responses.addAll(
                    this.handle(update)
            );
        }

        return responses;
    }
}
