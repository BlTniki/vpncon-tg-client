package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.ModelForRequest;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeerForRequest extends ModelForRequest {
    /**
     * Название конфигурационного файла. Должен состоять только из латиницы и/или цифр.
     */
    private String peerConfName;

    /**
     * Внутренний адрес в сети. Формат Х.Х.Х.Х. Может быть null, тогда peerIp сгенерирует самостоятельно.
     * Последний октет должен быть в пределах [2, 254].
     */
    private String peerIp;

    /**
     * Id юзера, для которого создаётся конфиг.
     */
    private Long userId;

    /**
     * Id хоста, на котором создаётся пир.
     */
    private Long hostId;
}
