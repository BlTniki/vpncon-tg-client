package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.exception.RequestServiceException;
import com.bitniki.VPNconTGclient.exception.notFoundException.UserNotFoundException;
import com.bitniki.VPNconTGclient.exception.validationFailedException.UserValidationFailedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;

public abstract class BranchWithUser extends Branch{
    protected UserEntity userEntity;
    public BranchWithUser(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public UserEntity loadUserByTelegramId(Long telegramId)
            throws RequestServiceException, UserNotFoundException, UserValidationFailedException {
        return requestService.getUserByTelegramId(telegramId);
    }
}
