package com.github.lzy.hotreload;

import static com.github.liuzhengyang.hotreload.boot.HotReloadFileUtils.findLocalHotReloadAgentCoreJarFiles;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/4/21
 */
public class HotReloadAgent {

    private static volatile ClassLoader hotReloadClassLoader;

    private static final String HOT_RELOAD_WORKER_CLASS = "com.github.lzy.hotreload.HotReloadWorker";
    private static final String HOT_RELOAD_RELOAD_METHOD = "doReload";

    public static void agentmain(String agentArgs, Instrumentation instrumentation)
            throws IOException, ClassNotFoundException {
        if (agentArgs == null) {
            throw new IllegalArgumentException("Agent args is null");
        }
        String[] splits = agentArgs.split(",");
        if (splits.length < 2) {
            throw new IllegalArgumentException(agentArgs);
        }

        File agentJarFile = findLocalHotReloadAgentCoreJarFiles();
        if (hotReloadClassLoader == null) {
            hotReloadClassLoader = new HotReloadClassLoader(
                    new URL[]{agentJarFile.toURL()});
        }

        System.out.println("agent core urls is " + agentJarFile.toURL());
        Class<?> hotReloadWorkerClass = hotReloadClassLoader.loadClass(HOT_RELOAD_WORKER_CLASS);
        try {
            Method method = hotReloadWorkerClass.getDeclaredMethod(HOT_RELOAD_RELOAD_METHOD, Instrumentation.class, String[].class);
            method.invoke(null, instrumentation, splits);
        } catch (Exception e) {
            System.out.println(String.format("Reload failed %s", e));;
        }
    }

}
