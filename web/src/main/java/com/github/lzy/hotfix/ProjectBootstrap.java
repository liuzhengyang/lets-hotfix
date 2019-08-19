package com.github.lzy.hotfix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author liuzhengyang
 */
@SpringBootApplication
@EnableEurekaClient
public class ProjectBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ProjectBootstrap.class);
    }
}
