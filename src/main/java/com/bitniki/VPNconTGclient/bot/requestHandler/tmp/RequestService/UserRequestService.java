package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.UserEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.UserValidatorRegex;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl.UserForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelValidationFailedException;

public interface UserRequestService {
    /**
     * Получение юзера по его логину.
     * @param login Логин юзера.
     * @return Сущность юзера.
     * @throws ModelNotFoundException Если юзер с таким логином не найден.
     */
    UserEntity getUserByLogin(String login) throws ModelNotFoundException;

    /**
     * Получение юзера с сервера по его telegramId.
     * @param telegramId Телеграмм Id юзера.
     * @return Сущность Юзера.
     * @throws ModelNotFoundException Если юзер не найден по-данному telegramId.
     */
    UserEntity getUserByTelegramId(Long telegramId) throws ModelNotFoundException;

    /**
     * Получение паттерна валидации для полей юзера с сервера
     * @return Правила валидации.
     */
    UserValidatorRegex getValidatorFields();

    /**
     * Создание юзера на сервере.
     * @param model Необходимые поля для создания. Указать login и password.
     * @return Сущность юзера.
     * @throws ModelValidationFailedException Если поля не прошли валидацию.
     */
    UserEntity createUserOnServer(UserForRequest model) throws ModelValidationFailedException;

    /**
     * Привязка телеграмм аккаунта к юзеру.
     * @param model Необходимые поля. Указать login, telegrammId и telegrammNickname
     * @return Обновлённую сущность юзера.
     * @throws ModelValidationFailedException Если поля не прошли валидацию.
     * @throws ModelNotFoundException Если юзер с данным логином не найден.
     */
    UserEntity associateTelegramIdWithUser(UserForRequest model) throws ModelValidationFailedException, ModelNotFoundException;

    /**
     * Отвязка телеграмм аккаунта к юзеру.
     * @param login Логин юзера.
     * @return Обновлённую сущность юзера.
     * @throws ModelValidationFailedException Если поля не прошли валидацию.
     * @throws ModelNotFoundException Если юзер с данным логином не найден.
     */
    UserEntity dissociateTelegramIdFromUser(String login) throws ModelValidationFailedException, ModelNotFoundException;

    /**
     * Обновление юзера на сервере.
     * @param userId Id юзера.
     * @param model Поля для обновления.
     * @return Обновлённую сущность юзера.
     * @throws ModelValidationFailedException Если поля не прошли валидацию.
     * @throws ModelNotFoundException Если юзер с данным Id не найден.
     */
    UserEntity updateUserOnServer(Long userId, UserForRequest model) throws ModelValidationFailedException, ModelNotFoundException;

    /**
     * Удаление юзера на сервере.
     * @param userId Id юзера.
     * @return Удалённую сущность юзера.
     * @throws ModelNotFoundException Если юзер с данным Id не найден.
     */
    UserEntity deleteUserOnServer(Long userId) throws ModelNotFoundException;
}
