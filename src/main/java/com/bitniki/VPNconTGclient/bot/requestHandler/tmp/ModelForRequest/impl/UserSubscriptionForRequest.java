package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.ModelForRequest;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscriptionForRequest extends ModelForRequest {
    /**
     * Id юзера
     */
    private Long userId;

    /**
     * Id подписки
     */
    private Long subscriptionId;
}
