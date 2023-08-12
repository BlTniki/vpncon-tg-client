package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.Model;
import lombok.*;

import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserValidatorRegex extends Model {
    private String login;
    private String password;

    /**
     * Валидация логина по полученному паттерну.
     * @param login Логин, который следует проверить
     * @return True, Если валидация НЕ ПРОШЛА.
     */
    public Boolean isLoginNotValid(String login) {
        Pattern loginPattern = Pattern.compile(this.login);

        return !loginPattern.matcher(login).matches();
    }

    /**
     * Валидация пароля по полученному паттерну.
     * @param password Пароль, который следует проверить
     * @return True, Если валидация НЕ ПРОШЛА.
     */
    public Boolean isPasswordNotValid(String password) {
        Pattern passwordPattern = Pattern.compile(this.password);

        return !passwordPattern.matcher(password).matches();
    }
}
