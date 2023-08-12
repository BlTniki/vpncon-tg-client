package com.bitniki.VPNconTGclient.bot.request.RequestService;

import com.bitniki.VPNconTGclient.bot.request.exception.ModelNotFoundException;

public interface PaymentRequestService {
    /**
     * Создание URL для оплаты подписки.
     * @param userId Id юзера, к которому следует добавить подписку.
     * @param subsId Id подписки, которую необходимо оплатить.
     * @return URL оплаты в виде строки.
     * @throws ModelNotFoundException Если юзер или подписка не найдена.
     */
    String makePaymentUrl(Long userId, Long subsId) throws ModelNotFoundException;
}
