package com.bitniki.VPNconTGclient.config;


import com.bitniki.VPNconTGclient.bot.UpdateRouter;
import com.bitniki.VPNconTGclient.bot.VPNconBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class VPNconBotConfig {
    @Value("${tg.botToken}")
    private String botToken;

    @Value("${tg.botUsername}")
    private String botUsername;

    @Autowired
    private UpdateRouter updateRouter;

    /**
     * init bot and run updates listener
     * @return TelegramBotsApi
     * @throws TelegramApiException
     */
    @Bean
    public TelegramBotsApi telegramBotsApiBean() throws TelegramApiException {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new VPNconBot(botToken, botUsername, updateRouter));
            return botsApi;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
