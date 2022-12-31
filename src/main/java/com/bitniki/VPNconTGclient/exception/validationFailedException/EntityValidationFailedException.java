package com.bitniki.VPNconTGclient.exception.validationFailedException;

import com.bitniki.VPNconTGclient.exception.BranchBadUpdateProvidedException;

public class EntityValidationFailedException extends BranchBadUpdateProvidedException {
    public EntityValidationFailedException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage().replace("400 : ","").replace("<EOL>", "\n");
    }
}
