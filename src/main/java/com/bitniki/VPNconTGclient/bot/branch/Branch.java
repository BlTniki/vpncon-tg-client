package com.bitniki.VPNconTGclient.bot.branch;

import com.bitniki.VPNconTGclient.bot.Responses;
import com.bitniki.VPNconTGclient.service.RequestService;
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
