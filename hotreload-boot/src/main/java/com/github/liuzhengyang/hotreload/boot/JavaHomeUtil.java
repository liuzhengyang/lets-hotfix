package com.github.liuzhengyang.hotreload.boot;

import java.io.File;

/**
 * @author liuzhengyang
 * Created on 2020-01-31
 */
public class JavaHomeUtil {

    private static volatile String FOUND_JAVA_HOME = null;

    public static String getJavaHomeDir() {
        if (FOUND_JAVA_HOME != null) {
            return FOUND_JAVA_HOME;
        }

        String javaHome = System.getProperty("java.home");
        if (JavaVersionUtil.isLessThanJava9()) {
            File toolsJar = new File(javaHome, "lib/tools.jar");
            if (!toolsJar.exists()) {
                toolsJar = new File(javaHome, "../lib/tools.jar");
            }
            if (!toolsJar.exists()) {
                toolsJar = new File("../../lib/tools.jar");
            }
            if (!toolsJar.exists()) {
                // TODO use System env JAVA_HOME
                throw new IllegalArgumentException("Can not find tools.jar under java home");
            }
            FOUND_JAVA_HOME = javaHome;
        } else {
            FOUND_JAVA_HOME = javaHome;
        }
        return FOUND_JAVA_HOME;
    }

    public static File getToolsJar() {
        if (!JavaVersionUtil.isLessThanJava9()) {
            return null;
        }

        String javaHomeDir = getJavaHomeDir();
        File toolsJar = new File(javaHomeDir, "lib/tools.jar");
        if (!toolsJar.exists()) {
            toolsJar = new File(javaHomeDir, "../lib/tools.jar");
        }
        if (!toolsJar.exists()) {
            toolsJar = new File("../../lib/tools.jar");
        }
        if (!toolsJar.exists()) {
            throw new IllegalArgumentException("Can not find tools.jar under java.home");
        }
        return toolsJar;
    }
}
