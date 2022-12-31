package com.bitniki.VPNconTGclient.exception.alreadyExistException;

import com.bitniki.VPNconTGclient.exception.BranchBadUpdateProvidedException;

public class EntityAlreadyExistException extends BranchBadUpdateProvidedException {
    public EntityAlreadyExistException(String message) {
        super(message);
    }
}
