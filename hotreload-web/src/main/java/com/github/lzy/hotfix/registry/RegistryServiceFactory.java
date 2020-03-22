package com.github.lzy.hotfix.registry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/22
 */
@Configuration
public class RegistryServiceFactory {

    @Value("${eureka.client.enabled}")
    private boolean enableEureka;

    @Bean
    public RegistryService registryService() {
        if (enableEureka) {
            return new EurekaRegistryService();
        } else {
            return new LocalModeRegistryService();
        }
    }
}
