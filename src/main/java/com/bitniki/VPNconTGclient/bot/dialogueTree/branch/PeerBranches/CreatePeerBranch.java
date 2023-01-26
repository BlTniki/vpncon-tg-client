package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.PeerBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.BranchWithUser;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestServiceException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.response.Response;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class CreatePeerBranch extends BranchWithUser {

    public CreatePeerBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    public CreatePeerBranch(BranchWithUser prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Override
    protected List<Response<?>> makeResponses(Update update) throws RequestServiceException, BranchBadUpdateProvidedException {
        return null;
    }
}
