package com.bitniki.VPNconTGclient.exception.notFoundException;

import com.bitniki.VPNconTGclient.exception.BranchBadUpdateProvidedException;

public class EntityNotFoundException extends BranchBadUpdateProvidedException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
