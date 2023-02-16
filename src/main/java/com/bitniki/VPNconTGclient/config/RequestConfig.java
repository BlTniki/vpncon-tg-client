package com.bitniki.VPNconTGclient.config;

import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class RequestConfig {
    @Value("${VPNconServer.address}")
    private String VPNconAddress;
    @Value("${VPNconServer.domain}")
    private String VPNconDomain;
    @Value("${VPNconServer.login}")
    private String botLogin;
    @Value("${VPNconServer.password}")
    private String botPassword;

    private RequestService requestService;

    /**
     * Configure Request bean
     */
    @Bean
    public RequestService requestServiceBean() {
        requestService = new RequestService(VPNconAddress, VPNconDomain, botLogin, botPassword);
        return requestService;
    }

    /**
     * Update bot token every day at 00:00
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateToken() {
        requestService.refreshBotToken();
    }
}
