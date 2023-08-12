package com.bitniki.VPNconTGclient.bot.request.Model.impl;

import com.bitniki.VPNconTGclient.bot.request.Model.Model;
import lombok.*;

import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeerValidatorRegex extends Model {
    private String peerIp;
    private String peerConfName;

    /**
     * Валидация названия конфига.
     * @param peerConfName Название конфига.
     * @return True, если валидация НЕ ПРОЙДЕНА.
     */
    public Boolean isPeerConfNameNotValid(String peerConfName) {
        Pattern passwordPattern = Pattern.compile(this.peerConfName);

        return !passwordPattern.matcher(peerConfName).matches();
    }
}
