package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.ModelForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.enumTypes.Operation;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetacodeForRequest extends ModelForRequest {
    /**
     * Код, по которому проверяется разрешение на действие.
     */
    private String code;

    /**
     * Действие, которое следует сделать.
     */
    private Operation operation;
}
