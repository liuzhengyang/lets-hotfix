package com.github.lzy.hotfix.registry;

import java.util.List;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/22
 */
public interface RegistryService {
    List<HotReloadInstance> findAllInstances();
}
