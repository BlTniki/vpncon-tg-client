package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Reminder;

import java.util.List;

public interface ReminderRequestService {
    /**
     * Получает напоминания об оплате с сервера.
     * @return Список напоминаний.
     */
    List<Reminder> getRemindersFromServer();
}
