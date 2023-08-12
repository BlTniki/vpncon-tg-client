package com.bitniki.VPNconTGclient.bot.request.Model.impl;

import com.bitniki.VPNconTGclient.bot.request.Model.Model;
import com.bitniki.VPNconTGclient.bot.request.enumTypes.Role;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends Model {
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
