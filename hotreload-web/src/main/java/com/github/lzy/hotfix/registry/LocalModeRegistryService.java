package com.github.lzy.hotfix.registry;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.github.lzy.hotfix.util.HostUtils;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/22
 */
public class LocalModeRegistryService implements RegistryService {

    @Value("${server.port}")
    private int port;

    @Override
    public List<HotReloadInstance> findAllInstances() {
        String hostName = HostUtils.getHostName();
        String homePageUrl = String.format("http:%s:%d/", hostName, port);
        HotReloadInstance hotReloadInstance = new HotReloadInstance(homePageUrl, hostName, port);
        return Collections.singletonList(hotReloadInstance);
    }
}
