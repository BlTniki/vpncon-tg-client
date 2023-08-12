package com.bitniki.VPNconTGclient.bot.request.ModelForRequest.impl;

import com.bitniki.VPNconTGclient.bot.request.ModelForRequest.ModelForRequest;
import com.bitniki.VPNconTGclient.bot.request.enumTypes.Operation;
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
