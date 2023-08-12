package com.bitniki.VPNconTGclient.bot.request.RequestService;

import com.bitniki.VPNconTGclient.bot.request.Model.impl.Reminder;

import java.util.List;

public interface ReminderRequestService {
    /**
     * Получает непрочитанные напоминания об оплате с сервера.
     * @return Список напоминаний.
     */
    List<Reminder> getRemindersFromServer();
}
