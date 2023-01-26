package com.bitniki.VPNconTGclient.bot.exception.notFoundException;

import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;

public class EntityNotFoundException extends BranchBadUpdateProvidedException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
