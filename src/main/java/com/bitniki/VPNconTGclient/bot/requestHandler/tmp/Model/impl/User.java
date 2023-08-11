package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.Model;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.enumTypes.Role;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Model {
    private Long id;

    private String login;

    /**
     * Роль юзера в сервисе. Определяет доступ к функционалу.
     */
    private Role role;

    /**
     * Id пользователя в Телеграм
     */
    private Long telegramId;

    /**
     * Ник пользователя в Телеграм
     */
    private String telegramNickname;
}
