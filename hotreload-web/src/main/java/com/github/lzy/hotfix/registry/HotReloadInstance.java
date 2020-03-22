package com.github.lzy.hotfix.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/22
 */
@Data
@AllArgsConstructor
public class HotReloadInstance {
    private String homePageUrl;
    private String hostName;
    private int port;
}
