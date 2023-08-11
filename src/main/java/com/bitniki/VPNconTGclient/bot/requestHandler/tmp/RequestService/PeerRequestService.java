package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Peer;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.PeerValidatorRegex;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl.PeerForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelValidationFailedException;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.List;

public interface PeerRequestService {
    /**
     * Получение всех пиров юзера.
     * @param userId Id юзера.
     * @return Список пиров юзера.
     */
    List<Peer> getPeersByUserId(Long userId);

    /**
     * Получение паттерна валидации для полей пира с сервера
     * @return Правила валидации.
     */
    PeerValidatorRegex getValidatorFields();

    /**
     * Получение пира с сервера по его peerIp и hostId.
     * @param peerIp Ip адрес пира.
     * @param hostId Id хоста.
     * @return Сущность пира.
     */
    Peer getPeerByPeerIpAndHostId(String peerIp, Long hostId) throws ModelNotFoundException;

    /**
     * Создание пира.
     * @param model Поля для создания пира.
     * @return Сущность нового пира.
     * @throws ModelValidationFailedException Если поля не прошли проверку.
     * @throws ModelNotFoundException Если хост или юзер не найден.
     */
    Peer createPeerOnServer(PeerForRequest model) throws ModelValidationFailedException, ModelNotFoundException;

    /**
     * Активация пира на хосте.
     * @param peerId Id пира.
     * @return True при активации.
     * @throws ModelNotFoundException Если пир не найден.
     */
    Boolean activatePeer(Long peerId) throws ModelNotFoundException;

    /**
     * Деактивация пира на хосте.
     * @param peerId Id пира.
     * @return True при деактивации.
     * @throws ModelNotFoundException Если пир не найден.
     */
    Boolean deactivatePeer(Long peerId) throws ModelNotFoundException;

    /**
     * Получение файла конфигурации с хоста.
     * @param peer Сущность пира.
     * @return Файл конфигурации.
     * @throws ModelNotFoundException Если пир не найден.
     */
    InputFile getConfigFileFromServer(Peer peer) throws ModelNotFoundException;

    /**
     * Удаление пира на сервере.
     * @param peerId Id пира.
     * @return Сущность удалённого пира.
     * @throws ModelNotFoundException Если пир не найден.
     */
    Peer deletePeerOnServer(Long peerId) throws ModelNotFoundException;
}
