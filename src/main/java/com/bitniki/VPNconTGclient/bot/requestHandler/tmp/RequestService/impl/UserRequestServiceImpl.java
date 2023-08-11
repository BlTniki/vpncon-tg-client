package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.User;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.UserValidatorRegex;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl.UserForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.UserRequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelValidationFailedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler400Exception;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler404Exception;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandlerException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.RequestHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserRequestServiceImpl implements UserRequestService {
    private final RequestHandler requestHandler;

    public UserRequestServiceImpl(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public User getUserByLogin(String login) throws ModelNotFoundException {
        final String ENDPOINT = "/users/byLogin/" + login;

        // check for uniques
        try {
            return requestHandler.GET(ENDPOINT, User.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с логином %s".formatted(login));
        } catch (RequestHandlerException e) {
            log.error("Неожиданная ошибка при попытке получить юзера по логину:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public User getUserByTelegramId(Long telegramId) throws ModelNotFoundException {
        final String ENDPOINT = "/users/tg/" + telegramId;

        try {
            return requestHandler.GET(ENDPOINT, User.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с telegramId = %d".formatted(telegramId));
        } catch (RequestHandlerException e) {
            log.error("Неожиданная ошибка при попытке получить юзера по telegramId:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public UserValidatorRegex getValidatorFields() {
        final String ENDPOINT = "/users/validator";

        try {
            return requestHandler.GET(ENDPOINT, UserValidatorRegex.class);
        } catch (RequestHandlerException e) {
            log.error("Неожиданная ошибка при попытке получить правила валидации юзера:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public User createUserOnServer(UserForRequest model) throws ModelValidationFailedException {
        final String ENDPOINT = "/users";

        // create user on server
        try {
            return requestHandler.POST(ENDPOINT, model, User.class);
        } catch (RequestHandler400Exception e) {
            throw new ModelValidationFailedException("Некоторые данные указаны неверно:\n" + e.getMessage());
        } catch (RequestHandlerException e) {
            log.error("Неожиданная ошибка при попытке создать юзера:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public User associateTelegramIdWithUser(UserForRequest model) throws ModelValidationFailedException, ModelNotFoundException {
        final String ENDPOINT = "/users/tg";

        // associate user on server
        try {
            return requestHandler.POST(ENDPOINT, model, User.class);
        } catch (RequestHandler400Exception e) {
            throw new ModelValidationFailedException("Некоторые данные указаны неверно:\n" + e.getMessage());
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с логином %s".formatted(model.getLogin()));
        } catch (RequestHandlerException e) {
            log.error("Неожиданная ошибка при попытке создать юзера:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public User dissociateTelegramIdFromUser(String login) throws ModelValidationFailedException, ModelNotFoundException {
        final String ENDPOINT = "/users/tg/" + login;

        // dissociate user on server
        try {
            return requestHandler.DELETE(ENDPOINT, User.class);
        } catch (RequestHandler400Exception e) {
            throw new ModelValidationFailedException("Некоторые данные указаны неверно:\n" + e.getMessage());
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с логином %s".formatted(login));
        } catch (RequestHandlerException e) {
            log.error("Неожиданная ошибка при попытке создать юзера:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public User updateUserOnServer(Long userId, UserForRequest model) throws ModelValidationFailedException, ModelNotFoundException {
        final String ENDPOINT = "/users/" + userId;

        // create user on server
        try {
            return requestHandler.PUT(ENDPOINT, model, User.class);
        } catch (RequestHandler400Exception e) {
            throw new ModelValidationFailedException("Некоторые данные указаны неверно:\n" + e.getMessage());
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с id %d".formatted(userId));
        } catch (RequestHandlerException e) {
            log.error("Неожиданная ошибка при попытке создать юзера:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public User deleteUserOnServer(Long userId) throws ModelNotFoundException {
        final String ENDPOINT = "/users/" + userId;

        // dissociate user on server
        try {
            return requestHandler.DELETE(ENDPOINT, User.class);
        }  catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Не смог найти юзера с id %d".formatted(userId));
        } catch (RequestHandlerException e) {
            log.error("Неожиданная ошибка при попытке создать юзера:\n" + e.getMessage());
            throw e;
        }
    }
}
