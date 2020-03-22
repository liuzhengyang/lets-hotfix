package com.github.lzy.hotfix.registry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.lzy.hotfix.util.ApplicationContextHolder;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/22
 */
public class EurekaRegistryService implements RegistryService {

    private static final String APPLICATION_NAME = "LETS-HOTFIX";

    @Override
    public List<HotReloadInstance> findAllInstances() {
        EurekaClient eurekaClient = ApplicationContextHolder.getBean(EurekaClient.class);
        Application application = eurekaClient.getApplication(APPLICATION_NAME);
        return Optional.ofNullable(application)
                .map(Application::getInstances)
                .orElse(Collections.emptyList())
                .stream()
                .map(instanceInfo -> new HotReloadInstance(instanceInfo.getHomePageUrl(), instanceInfo.getHostName(), instanceInfo.getPort()))
                .collect(Collectors.toList());
    }
}
