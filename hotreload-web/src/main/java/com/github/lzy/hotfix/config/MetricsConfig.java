package com.github.lzy.hotfix.config;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.lzy.hotfix.util.HostUtils;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * @author liuzhengyang
 */
@Configuration
public class MetricsConfig {
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("host", HostUtils.getHostName());
    }
}
