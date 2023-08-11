package com.bitniki.VPNconTGclient.config;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandlerException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.RequestHandler;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.impl.RequestHandlerImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class RequestConfig {
    @Value("${VPNconServer.address}")
    private String VPNCON_ADDRESS;
    @Value("${VPNconServer.login}")
    private String BOT_LOGIN;
    @Value("${VPNconServer.password}")
    private String BOT_PASSWORD;

    private RequestHandler requestHandler;

    @Bean
    public RequestHandler requestHandlerBean() {
        this.requestHandler =  new RequestHandlerImpl(
                VPNCON_ADDRESS,
                BOT_LOGIN,
                BOT_PASSWORD
        );
        return this.requestHandler;
    }

    /**
     * Update bot token every day at 00:00
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateToken() throws RequestHandlerException {
        requestHandler.SignInAndMakeHeaders();
    }

//    private RequestService requestService;

//    /**
//     * Configure Request bean
//     */
//    @Bean
//    public RequestService requestServiceBean() {
//        requestService = new RequestService(VPNconAddress, VPNconDomain, botLogin, botPassword);
//        return requestService;
//    }
}
