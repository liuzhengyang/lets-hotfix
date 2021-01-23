package com.github.liuzhengyang.hotreload.boot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.github.liuzhengyang.hotreload.VersionUtils;

/**
 * @author liuzhengyang
 * 2021/1/24
 */
public class HotReloadFileUtils {

    private static final String ALI_YUN_MAVEN_PREFIX = "https://maven.aliyun.com/repository/public";

    public static File findLocalHotReloadWebJarFiles() {
        File targetFile = new File(System.getProperty("user.home") + "/.hotreload/", "hotreload-web-" + VersionUtils.getLatestVersion() + ".jar");
        targetFile.getParentFile().mkdirs();
        if (targetFile.exists()) {
            return targetFile;
        }
        System.out.println(String.format("HotReload web jar not found %s, downloading...", targetFile.getAbsolutePath()));
        String url = ALI_YUN_MAVEN_PREFIX + "/com/github/liuzhengyang/hotreload-web/" + VersionUtils.getLatestVersion() + "/hotreload-web-" + VersionUtils.getLatestVersion() + ".jar";
        try {
            InputStream inputStream = openUrlStream(url);
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File findLocalHotReloadAgentJarFiles() {
        File targetFile = new File(System.getProperty("user.home") + "/.hotreload/", "hotreload-agent-" + VersionUtils.getLatestVersion() + "-jar-with-dependencies.jar");
        targetFile.getParentFile().mkdirs();
        if (targetFile.exists()) {
            return targetFile;
        }
        System.out.println(String.format("HotReload Agent jar not found %s, downloading...", targetFile.getAbsolutePath()));
        String url = ALI_YUN_MAVEN_PREFIX + "/com/github/liuzhengyang/hotreload-agent/" + VersionUtils.getLatestVersion() + "/hotreload-agent-" + VersionUtils.getLatestVersion() + "-jar-with-dependencies.jar";
        try {
            InputStream inputStream = openUrlStream(url);
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File findLocalHotReloadAgentCoreJarFiles() {
        File targetFile = new File(System.getProperty("user.home") + "/.hotreload/", "hotreload-core-" + VersionUtils.getLatestVersion() + "-jar-with-dependencies.jar");
        targetFile.getParentFile().mkdirs();
        if (targetFile.exists()) {
            return targetFile;
        }
        System.out.println(String.format("HotReload Agent core jar not found %s, downloading...", targetFile.getAbsolutePath()));
        String url = ALI_YUN_MAVEN_PREFIX + "/com/github/liuzhengyang/hotreload-core/" + VersionUtils.getLatestVersion() + "/hotreload-core-" + VersionUtils.getLatestVersion() + "-jar-with-dependencies.jar";
        try {
            InputStream inputStream = openUrlStream(url);
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static InputStream openUrlStream(String url) throws IOException {
        URL targetUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            String location = conn.getHeaderField("Location");
            return openUrlStream(location);
        }
        return conn.getInputStream();
    }
}
