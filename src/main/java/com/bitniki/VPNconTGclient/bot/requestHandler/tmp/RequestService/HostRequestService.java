package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Host;

import java.util.List;

public interface HostRequestService {
    /**
     * Получение списка хостов с сервера.
     * @return Список хостов.
     */
    List<Host> getHostsFromServer();
}
