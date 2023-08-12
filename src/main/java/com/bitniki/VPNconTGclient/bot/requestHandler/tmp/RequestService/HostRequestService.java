package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Host;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;

import java.util.List;

public interface HostRequestService {
    /**
     * Получение списка хостов с сервера.
     * @return Список хостов.
     */
    List<Host> getHostsFromServer();

    /**
     * Получение кол-ва доступных пиров на сервере.
     * @param hostId Id хоста.
     * @return Кол-во доступных пиров.
     * @throws ModelNotFoundException Если хост не найден.
     */
    Integer countAvailablePeersOnHost(Long hostId) throws ModelNotFoundException;
}
