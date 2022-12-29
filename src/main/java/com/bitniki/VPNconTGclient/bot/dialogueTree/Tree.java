package com.bitniki.VPNconTGclient.bot.dialogueTree;

import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.InitBranch;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class Tree {
    private UserEntity userEntity;
    private Branch currentBranch;
    private RequestService requestService;

    public Tree(RequestService requestService) {
        this.requestService = requestService;
        this.currentBranch = new InitBranch(null, requestService);
    }

    public List<Response<?>> handle(Update update) {
        // Get responses from branch
        List<Response<?>> responses = this.currentBranch.handle(update);

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
