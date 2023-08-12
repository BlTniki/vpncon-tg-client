package com.bitniki.VPNconTGclient.config;

import com.bitniki.VPNconTGclient.bot.request.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.router.UpdateRouter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UpdateRouterConfig {
    @Bean
    public UpdateRouter updateRouterBean(RequestServiceFactory requestService) {
        return new UpdateRouter(requestService);
    }
}
