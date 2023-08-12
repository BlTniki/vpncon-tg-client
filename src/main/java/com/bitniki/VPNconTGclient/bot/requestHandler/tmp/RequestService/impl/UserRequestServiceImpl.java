package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Token;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.UserEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.UserValidatorRegex;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl.UserForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.UserRequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelValidationFailedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler400Exception;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler404Exception;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.RequestHandler;

public class UserRequestServiceImpl implements UserRequestService {
    private final RequestHandler requestHandler;

    public UserRequestServiceImpl(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public UserEntity getUserByLogin(String login) throws ModelNotFoundException {
        final String ENDPOINT = "/users/byLogin/" + login;

        // check for uniques
        try {
            return requestHandler.GET(ENDPOINT, UserEntity.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с логином %s".formatted(login));
        }
    }

    @Override
    public UserEntity getUserByTelegramId(Long telegramId) throws ModelNotFoundException {
        final String ENDPOINT = "/users/tg/" + telegramId;

        try {
            return requestHandler.GET(ENDPOINT, UserEntity.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с telegramId = %d".formatted(telegramId));
        }
    }

    @Override
    public UserValidatorRegex getValidatorFields() {
        final String ENDPOINT = "/users/validator";

        return requestHandler.GET(ENDPOINT, UserValidatorRegex.class);
    }

    @Override
    public UserEntity createUserOnServer(UserForRequest model) throws ModelValidationFailedException {
        final String ENDPOINT = "/users";

        // create user on server
        try {
            return requestHandler.POST(ENDPOINT, model, UserEntity.class);
        } catch (RequestHandler400Exception e) {
            throw new ModelValidationFailedException("Некоторые данные указаны неверно:\n" + e.getMessage());
        }
    }

    @Override
    public UserEntity associateTelegramIdWithUser(UserForRequest model) throws ModelValidationFailedException, ModelNotFoundException {
        final String ENDPOINT = "/users/tg";

        // associate user on server
        try {
            return requestHandler.POST(ENDPOINT, model, UserEntity.class);
        } catch (RequestHandler400Exception e) {
            throw new ModelValidationFailedException("Некоторые данные указаны неверно:\n" + e.getMessage());
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с логином %s".formatted(model.getLogin()));
        }
    }

    @Override
    public UserEntity dissociateTelegramIdFromUser(String login) throws ModelNotFoundException {
        final String ENDPOINT = "/users/tg/" + login;

        // dissociate user on server
        try {
            return requestHandler.DELETE(ENDPOINT, UserEntity.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с логином %s".formatted(login));
        }
    }

    @Override
    public UserEntity updateUserOnServer(Long userId, UserForRequest model) throws ModelValidationFailedException, ModelNotFoundException {
        final String ENDPOINT = "/users/" + userId;

        // create user on server
        try {
            return requestHandler.PUT(ENDPOINT, model, UserEntity.class);
        } catch (RequestHandler400Exception e) {
            throw new ModelValidationFailedException("Некоторые данные указаны неверно:\n" + e.getMessage());
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с id %d".formatted(userId));
        }
    }

    @Override
    public UserEntity deleteUserOnServer(Long userId) throws ModelNotFoundException {
        final String ENDPOINT = "/users/" + userId;

        // dissociate user on server
        try {
            return requestHandler.DELETE(ENDPOINT, UserEntity.class);
        }  catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с id %d".formatted(userId));
        }
    }

    @Override
    public Boolean isSignInValid(UserForRequest model) {
        final String ENDPOINT = "/users/login";

        try {
            requestHandler.POST(ENDPOINT, model, Token.class);
        } catch (RequestHandler400Exception | RequestHandler404Exception e) {
            // if we catch those errors that means that sign in failed
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean isLoginIsNotUnique(String login) {
        try {
            getUserByLogin(login);
        } catch (ModelNotFoundException e) {
            // if we catch those errors that means that login is unique
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
