package com.bitniki.VPNconTGclient.bot.exception.alreadyExistException;

import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;

public class EntityAlreadyExistException extends BranchBadUpdateProvidedException {
    public EntityAlreadyExistException(String message) {
        super(message);
    }
}
