package com.github.liuzhengyang.hotreload.boot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/16
 */
public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    private static final String ALI_YUN_MAVEN_PREFIX = "https://maven.aliyun.com/repository/public";

    public static void main(String[] args) throws Exception {
        CommandLineParser defaultParser = new DefaultParser();
        Options options = new Options()
                .addOption("port", "port", true, "web port")
                .addOption("eurekaServer", "eurekaServer", true, "eurekaServer")
                .addOption("localMode", false, "don't register to eureka server")
                ;
        CommandLine commandLine = defaultParser.parse(options, args);
        String webPort = commandLine.getOptionValue("port");
        boolean localMode = commandLine.hasOption("localMode");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "18806";
        }
        String eurekaServer = null;
        if (!localMode) {
            eurekaServer = commandLine.getOptionValue("eurekaServer");
            if (eurekaServer == null || eurekaServer.isEmpty()) {
                System.out.println("No register server argument found. Usage: java -jar web.jar --eurekaServer xxxx:18086");
                return;
            } else {
                System.out.println("Running with server mode with registering server " + eurekaServer);
            }
        } else {
            System.out.println("Running with local mode");
        }

        Option.builder();
        List<String> command = new ArrayList<>();
        command.add("java");
        if (JavaVersionUtil.isLessThanJava9()) {
            File toolsJar = JavaHomeUtil.getToolsJar();
            if (toolsJar == null || !toolsJar.exists()) {
                logger.error("Tools jar not found");
                return;
            }
            command.add("-Xbootclasspath/a:" + JavaHomeUtil.getToolsJar().getAbsolutePath());
        }
        command.add("-jar");
        File agentJarFile = findLocalHotReloadAgentJarFiles();
        if (agentJarFile == null){
            logger.error("Agent jar not found");
            return;
        }
        command.add("-Dagent.path=" + agentJarFile.getAbsolutePath());
        command.add("-Dserver.port=" + webPort);
        if (localMode) {
            command.add("-Deureka.client.enabled=false");
        } else {
            command.add("-Deureka.client.enabled=true");
            command.add("-Deureka.client.service-url.defaultZone=http://" + eurekaServer + "/eureka/");
        }
        File localHotReloadWebJarFiles = findLocalHotReloadWebJarFiles();
        if (localHotReloadWebJarFiles == null){
            logger.error("Web jar not found");
            return;
        }
        command.add(localHotReloadWebJarFiles.getAbsolutePath());

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process start = processBuilder.start();
        InputStream inputStream = start.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = br.readLine()) != null) {
            System.out.println(line);
            if (line.contains("Started ProjectBootstrap")) {
                break;
            }
        }
        logger.info("HotReload web service started");
    }

    private static File findLocalHotReloadWebJarFiles() {
        File targetFile = new File(System.getProperty("user.home") + "/.hotreload/", "hotreload-web-" + getLatestVersion() + ".jar");
        targetFile.getParentFile().mkdirs();
        if (targetFile.exists()) {
            return targetFile;
        }
        logger.info("HotReload web jar not found {}, downloading...", targetFile.getAbsolutePath());
        String url = ALI_YUN_MAVEN_PREFIX + "/com/github/liuzhengyang/hotreload-web/" + getLatestVersion() + "/hotreload-web-" + getLatestVersion() + ".jar";
        try {
            InputStream inputStream = openUrlStream(url);
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File findLocalHotReloadAgentJarFiles() {
        File targetFile = new File(System.getProperty("user.home") + "/.hotreload/", "hotreload-agent-" + getLatestVersion() + "-jar-with-dependencies.jar");
        targetFile.getParentFile().mkdirs();
        if (targetFile.exists()) {
            return targetFile;
        }
        logger.info("HotReload Agent jar not found {}, downloading...", targetFile.getAbsolutePath());
        String url = ALI_YUN_MAVEN_PREFIX + "/com/github/liuzhengyang/hotreload-agent/" + getLatestVersion() + "/hotreload-agent-" + getLatestVersion() + "-jar-with-dependencies.jar";
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

    public static String getLatestVersion() {
        return "1.0.5";
    }
}
