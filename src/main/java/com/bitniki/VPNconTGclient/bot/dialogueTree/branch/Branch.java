package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.Responses;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class Branch {
    private Branch prevBranch;
    protected RequestService requestService;

    public Branch(Branch prevBranch, RequestService requestService) {
        this.prevBranch = prevBranch;
        this.requestService = requestService;
    }

    public abstract Responses handle(Update update);

    public Branch getPrevBranch() {
        return prevBranch;
    }

    public void setPrevBranch(Branch prevBranch) {
        this.prevBranch = prevBranch;
    }

    public RequestService getRequestService() {
        return requestService;
    }

    public void setRequestService(RequestService requestService) {
        this.requestService = requestService;
    }
}
