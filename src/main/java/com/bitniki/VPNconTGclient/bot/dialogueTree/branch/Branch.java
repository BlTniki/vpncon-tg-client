package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestServiceException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public abstract class Branch {
    private Branch prevBranch;
    private Branch nextBranch;
    private boolean isBranchWantChangeBranch;
    protected RequestService requestService;

    public Branch(RequestService requestService) {
        this.requestService = requestService;
    }

    public Branch(Branch prevBranch, RequestService requestService) {
        this.prevBranch = prevBranch;
        this.requestService = requestService;
    }

    public abstract List<Response<?>> handle(Update update)
            throws RequestServiceException, BranchBadUpdateProvidedException;

    public Branch getPrevBranch() {
        return prevBranch;
    }

    public void setPrevBranch(Branch prevBranch) {
        this.prevBranch = prevBranch;
    }

    public Branch getNextBranch() {
        return nextBranch;
    }

    public void setNextBranch(Branch nextBranch) {
        this.nextBranch = nextBranch;
        this.isBranchWantChangeBranch = true;
    }

    public boolean isBranchWantChangeBranch() {
        return isBranchWantChangeBranch;
    }

    public RequestService getRequestService() {
        return requestService;
    }

    public void setRequestService(RequestService requestService) {
        this.requestService = requestService;
    }
}
