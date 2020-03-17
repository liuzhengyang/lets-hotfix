package com.github.lzy.lethotfix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author liuzhengyang
 */
@SpringBootApplication
@EnableEurekaServer
public class RegistryProjectBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(RegistryProjectBootstrap.class);
    }
}
