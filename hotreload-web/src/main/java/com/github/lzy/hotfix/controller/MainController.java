package com.github.lzy.hotfix.controller;

import static java.util.stream.Collectors.toList;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

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
import com.github.lzy.hotfix.registry.HotReloadInstance;
import com.github.lzy.hotfix.registry.RegistryService;
import com.github.lzy.hotfix.service.HotfixService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author liuzhengyang
 */
@Controller
public class MainController {

    @Resource
    private HotfixService hotfixService;

    @Resource
    private AgentWebClient agentWebClient;

    @Resource
    private RegistryService registryService;

    @GetMapping("/")
    public String main(Model model) throws UnknownHostException {
        model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());
        List<HotReloadInstance> instances = registryService.findAllInstances();
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
        return Mono.justOrEmpty(registryService.findAllInstances())
                .flatMapMany(Flux::fromIterable)
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
