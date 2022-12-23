package com.bitniki.VPNconTGclient.config;

import com.bitniki.VPNconTGclient.bot.UpdateRouter;
import com.bitniki.VPNconTGclient.service.RequestService;
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
