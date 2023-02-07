package com.bitniki.VPNconTGclient.config;

import com.bitniki.VPNconTGclient.bot.BasicTelegramBot;
import com.bitniki.VPNconTGclient.bot.postman.Postman;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SuppressWarnings("unused")
@Configuration
@EnableScheduling
public class PostmanConfig {
    @Autowired
    private BasicTelegramBot bot;
    @Autowired
    private RequestService requestService;

    @Bean
    public Postman postmanBean() {
        return new Postman(bot, requestService);
    }
}
