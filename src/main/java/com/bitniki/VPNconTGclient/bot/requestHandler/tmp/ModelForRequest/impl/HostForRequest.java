package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.ModelForRequest;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostForRequest extends ModelForRequest {
    /**
     * Название сервера.
     */
    private String name;

    /**
     * Ip адрес до сервера. Комбинация айпи и порта должна быть уникальна.
     */
    private String ipaddress;

    /**
     * Порт, который слушает хост. Комбинация айпи и порта должна быть уникальна.
     */
    private Integer port;

    /**
     * Внутренний адрес сети (например 10.8.0.0)
     */
    private String hostInternalNetworkPrefix;

    /**
     * Пароль хоста.
     */
    private String hostPassword;

    /**
     * Публичный ключ Wireguard.
     */
    private String hostPublicKey;
}
