package com.github.liuzhengyang.hotreload.boot;

import static com.github.liuzhengyang.hotreload.boot.HotReloadFileUtils.findLocalHotReloadAgentCoreJarFiles;
import static com.github.liuzhengyang.hotreload.boot.HotReloadFileUtils.findLocalHotReloadAgentJarFiles;
import static com.github.liuzhengyang.hotreload.boot.HotReloadFileUtils.findLocalHotReloadWebJarFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        findLocalHotReloadAgentCoreJarFiles();
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
}
