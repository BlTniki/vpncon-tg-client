package com.bitniki.VPNconTGclient.config;

import com.bitniki.VPNconTGclient.bot.router.UpdateRouter;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UpdateRouterConfig {
    @Autowired
    private RequestService requestService;
    @Bean
    public UpdateRouter updateRouterBean() {
        return new UpdateRouter(requestService);
    }
}
