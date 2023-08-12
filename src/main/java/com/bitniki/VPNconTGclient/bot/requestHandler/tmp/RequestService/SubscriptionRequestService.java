package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Subscription;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.enumTypes.Role;

import java.util.List;

public interface SubscriptionRequestService {
    /**
     * Получение подписок по роли.
     * @param role Роль, для которой предназначается подписка.
     * @return Список подписок.
     */
    List<Subscription> getSubsByRole(Role role);
}
