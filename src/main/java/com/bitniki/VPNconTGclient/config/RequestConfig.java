package com.bitniki.VPNconTGclient.config;

import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestConfig {
    @Value("${VPNconServer.address}")
    private String VPNconAddress;
    @Value("${VPNconServer.domain}")
    private String VPNconDomain;
    @Value("${VPNconServer.login}")
    private String botLogin;
    @Value("${VPNconServer.password}")
    private String botPassword;

    /**
     * Configure Request bean
     */
    @Bean
    public RequestService requestServiceBean() {
        return new RequestService(VPNconAddress, VPNconDomain, botLogin, botPassword);
    }
}
