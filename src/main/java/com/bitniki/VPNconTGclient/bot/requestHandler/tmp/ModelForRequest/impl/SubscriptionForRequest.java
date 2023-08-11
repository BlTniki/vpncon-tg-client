package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.ModelForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.enumTypes.Role;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionForRequest extends ModelForRequest {
    /**
     * Роль юзера, для которого предназначена подписка
     */
    private Role role;

    /**
     * Цена в рублях
     */
    private Integer priceInRub = 0;

    /**
     * Максимум доступных к созданию пиров для юзера
     */
    private Integer peersAvailable = 0;

    /**
     * Длительность подписки в кол-ве дней
     */
    private Integer durationDays = 0;
}
