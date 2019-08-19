package com.github.lzy.hotfix.util;

import java.net.URISyntaxException;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

/**
 * @author liuzhengyang
 */
public class VMUtils {
    public static String getVmArgs(String pid) {
        try {
            return MonitoredVmUtil.jvmArgs(MonitoredHost.getMonitoredHost("localhost").getMonitoredVm(new VmIdentifier("//" + pid + "?mode=r")));
        } catch (MonitorException | URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }
}
