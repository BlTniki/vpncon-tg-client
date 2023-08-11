package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.Model;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token extends Model {
    /**
     * Логин юзера, для которого предназначен токен.
     */
    private String login;

    /**
     * Токен юзера.
     */
    private String token;
}
