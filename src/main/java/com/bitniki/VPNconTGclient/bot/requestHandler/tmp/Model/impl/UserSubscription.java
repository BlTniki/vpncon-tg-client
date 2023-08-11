package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.Model;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscription extends Model {
    private Long id;

    private User user;

    private Subscription subscription;

    /**
     * День сгорания подписки
     */
    private LocalDate expirationDay;
}
