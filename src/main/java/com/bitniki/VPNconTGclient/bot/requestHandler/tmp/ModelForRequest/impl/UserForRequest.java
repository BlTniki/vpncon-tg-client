package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.ModelForRequest;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserForRequest extends ModelForRequest {
    /**
     * Логин юзера. Уникальный. Должен быть минимум в 3 символа длиной,
     * начинаться с латинского символа и состоять из: латинского алфавита и, опционально, знаков "_" и "."
     */
    private String login;

    /**
     * Пароль юзера. Должен быть минимум в 6 символа длиной и содержать:
     * Строчную и прописную латинский символ,
     * Цифры
     */
    private String password;

    /**
     * Id юзера в Telegram.
     */
    private Long telegramId;

    /**
     * Nickname юзера в Telegram.
     */
    private String telegramNickname;
}
