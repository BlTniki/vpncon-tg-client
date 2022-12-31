package com.bitniki.VPNconTGclient.exception.validationFailedException;

import com.bitniki.VPNconTGclient.exception.BranchBadUpdateProvidedException;

public class EntityValidationFailedException extends BranchBadUpdateProvidedException {
    public EntityValidationFailedException(String message) {
        super(message);
    }
}
