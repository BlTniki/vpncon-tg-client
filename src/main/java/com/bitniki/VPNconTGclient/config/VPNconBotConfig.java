package com.bitniki.VPNconTGclient.config;


import com.bitniki.VPNconTGclient.bot.BasicTelegramBot;
import com.bitniki.VPNconTGclient.bot.router.UpdateRouter;
import com.bitniki.VPNconTGclient.bot.VPNconBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SuppressWarnings("unused")
@Configuration
public class VPNconBotConfig {
    @Value("${tg.botToken}")
    private String botToken;
    @Value("${tg.botUsername}")
    private String botUsername;

    @Autowired
    private UpdateRouter updateRouter;
    private BasicTelegramBot bot;

    /**
     * init bot and run updates listener
     * @return TelegramBotsApi
     * @throws TelegramApiException if something's goes wrong
     */
    @Bean
    public TelegramBotsApi telegramBotsApiBean() throws TelegramApiException {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            return botsApi;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Bean
    public BasicTelegramBot basicTelegramBotBean () {
        this.bot = new VPNconBot(botToken, botUsername, updateRouter);
        return bot;
    }
}
