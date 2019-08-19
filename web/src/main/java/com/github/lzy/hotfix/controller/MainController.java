package com.github.lzy.hotfix.controller;

import static java.util.stream.Collectors.toList;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.github.lzy.hotfix.model.HotfixResult;
import com.github.lzy.hotfix.model.JvmProcess;
import com.github.lzy.hotfix.model.Result;
import com.github.lzy.hotfix.proxy.AgentWebClient;
import com.github.lzy.hotfix.service.HotfixService;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

import reactor.core.publisher.Mono;

/**
 * @author liuzhengyang
 */
@Controller
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private static final String APPLICATION_NAME = "LETS-HOTFIX";

    @Resource
    private EurekaClient eurekaClient;

    @Resource
    private HotfixService hotfixService;

    @Resource
    private AgentWebClient agentWebClient;

    @GetMapping("/")
    public String main(Model model) throws UnknownHostException {
        model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());
        Application application = eurekaClient.getApplication(APPLICATION_NAME);
        List<InstanceInfo> instances = Optional.ofNullable(application)
                .map(Application::getInstances).orElse(Collections.emptyList());
        model.addAttribute("instances", instances);
        return "main";
    }

    @GetMapping("/processList")
    @ResponseBody
    public Mono<Result<List<JvmProcess>>> processFlux(@RequestParam(value = "proxyServer",
            required = false) String proxyServer) {
        return Mono.justOrEmpty(proxyServer)
                .flatMap(proxy -> agentWebClient.getJvmProcess(proxy))
                .switchIfEmpty(Mono.fromCallable(() -> Result.success(hotfixService.getProcessList())));
    }

    @GetMapping("/hostList")
    @ResponseBody
    public Mono<Result<List<String>>> hostList() {
        return Mono.justOrEmpty(eurekaClient.getApplication(APPLICATION_NAME))
                .flatMapIterable(Application::getInstances)
                .map(app -> String.join(":", app.getHostName(), String.valueOf(app.getPort())))
                .collect(toList())
                .defaultIfEmpty(Collections.emptyList())
                .map(Result::success);
    }

    @PostMapping("/hotfix")
    @ResponseBody
    public Mono<Result<HotfixResult>> hotfix(@RequestParam("file") MultipartFile file,
            @RequestParam("targetPid") String targetPid,
            @RequestParam(value = "proxyServer", required = false) String proxyServer) {
        return Mono.justOrEmpty(proxyServer)
                .flatMap(proxy -> agentWebClient.reloadClass(file, targetPid, proxy))
                .switchIfEmpty(Mono.fromCallable(() ->
                        Result.success(new HotfixResult(hotfixService.hotfix(file, targetPid)))));
    }
}