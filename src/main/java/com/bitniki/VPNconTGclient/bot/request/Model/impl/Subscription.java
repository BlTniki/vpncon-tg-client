package com.bitniki.VPNconTGclient.bot.request.Model.impl;

import com.bitniki.VPNconTGclient.bot.request.Model.Model;
import com.bitniki.VPNconTGclient.bot.request.enumTypes.Role;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription extends Model {
    private Long id;

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

    public String describe() {
        return  "Цена в рублях: " + priceInRub +
                ". \nКол-во доступных конфигов: " + peersAvailable +
                ". \nКол-во дней: " + durationDays;
    }
}
