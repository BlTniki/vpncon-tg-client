package com.bitniki.VPNconTGclient.bot.branch;

import com.bitniki.VPNconTGclient.bot.Responses;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class Branch {
    private Branch prevBranch;

    public Branch(Branch prevBranch) {
        this.prevBranch = prevBranch;
    }

    public abstract Responses handle(Update update);
}
