package com.bitniki.VPNconTGclient.bot.branch;

import com.bitniki.VPNconTGclient.bot.Response;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class Branch {
    private Branch prevBranch;

    public Branch(Branch prevBranch) {
        this.prevBranch = prevBranch;
    }

    public abstract Response handle(Update update);
}
