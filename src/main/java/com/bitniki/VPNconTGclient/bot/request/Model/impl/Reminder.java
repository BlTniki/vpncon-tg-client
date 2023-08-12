package com.bitniki.VPNconTGclient.bot.request.Model.impl;

import com.bitniki.VPNconTGclient.bot.request.Model.Model;
import com.bitniki.VPNconTGclient.bot.request.enumTypes.ReminderType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder extends Model {
    private Long id;
    private ReminderType reminderType;
    private UserEntity user;
    private String payload;
}
