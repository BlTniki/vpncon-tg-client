package com.bitniki.VPNconTGclient.config;

import com.bitniki.VPNconTGclient.bot.BasicTelegramBot;
import com.bitniki.VPNconTGclient.bot.postman.Postman;
import com.bitniki.VPNconTGclient.bot.request.RequestService.RequestServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class PostmanConfig {
    @Bean
    public Postman postmanBean(BasicTelegramBot bot, RequestServiceFactory requestService) {
        return new Postman(bot, requestService);
    }
}
