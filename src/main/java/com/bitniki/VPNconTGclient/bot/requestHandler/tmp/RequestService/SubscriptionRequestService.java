package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Subscription;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.UserSubscription;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.enumTypes.Role;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;

import java.util.List;

public interface SubscriptionRequestService {
    /**
     * Получение подписок по роли.
     * @param role Роль, для которой предназначается подписка.
     * @return Список подписок.
     */
    List<Subscription> getSubsByRole(Role role);

    /**
     * Получение подписки юзера по его Id.
     * @param userId Id юзера.
     * @return Подписка юзера.
     * @throws ModelNotFoundException если юзер или его подписка не найдена.
     */
    UserSubscription getUserSubscriptionByUserId(Long userId) throws ModelNotFoundException;
}
