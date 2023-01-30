package com.bitniki.VPNconTGclient.bot.exception;

/*
This exception assumes repeat last input
 */
public class BranchBadUpdateProvidedException extends BranchCriticalException {
    public BranchBadUpdateProvidedException(String message) {
        super(message);
    }
}
