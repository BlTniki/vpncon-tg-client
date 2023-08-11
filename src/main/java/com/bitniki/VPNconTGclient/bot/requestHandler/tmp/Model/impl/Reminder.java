package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.Model;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.enumTypes.ReminderType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder extends Model {
    private Long id;
    private ReminderType reminderType;
    private User user;
    private String payload;
}
