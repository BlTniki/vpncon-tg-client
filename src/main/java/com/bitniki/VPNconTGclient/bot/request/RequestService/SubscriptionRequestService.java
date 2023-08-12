package com.bitniki.VPNconTGclient.bot.request.RequestService;

import com.bitniki.VPNconTGclient.bot.request.enumTypes.Role;
import com.bitniki.VPNconTGclient.bot.request.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.Subscription;
import com.bitniki.VPNconTGclient.bot.request.Model.impl.UserSubscription;

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
