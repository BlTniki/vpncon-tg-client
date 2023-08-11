package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.Model;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Host extends Model {
    private Long id;

    /**
     * Название сервера.
     */
    private String name;

    /**
     * Ip адрес до сервера.
     */
    private String ipaddress;

    /**
     * Порт, который слушает хост.
     */
    private Integer port;

    /**
     * Внутренний адрес сети (например 10.8.0.0)
     */
    private String hostInternalNetworkPrefix;

    /**
     * Публичный ключ Wireguard.
     */
    private String hostPublicKey;
}
