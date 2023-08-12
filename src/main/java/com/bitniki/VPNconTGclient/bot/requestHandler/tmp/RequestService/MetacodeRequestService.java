package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl.MetacodeForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;

public interface MetacodeRequestService {
    /**
     * Применение метакода юзера по его логину.
     * @param login Логин юзера.
     * @param model {@link MetacodeForRequest} с указанным полем code.
     * @return строчку "Success".
     * @throws ModelNotFoundException если юзер или метакод не найден.
     */
    String useCodeOnServerByLogin(String login, MetacodeForRequest model) throws ModelNotFoundException;
}
