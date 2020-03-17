package com.github.lzy.hotfix.proxy;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.lzy.hotfix.model.HotfixResult;
import com.github.lzy.hotfix.model.JvmProcess;
import com.github.lzy.hotfix.model.Result;

import reactor.core.publisher.Mono;

/**
 * @author liuzhengyang
 */
@Service
public class AgentWebClient {

    public Mono<Result<List<JvmProcess>>> getJvmProcess(String proxyServer) {
        WebClient webClient = WebClient.create(proxyServer);
        return webClient.get()
                .uri("/processList")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Result<List<JvmProcess>>>(){});
    }

    public Mono<Result<HotfixResult>> reloadClass(MultipartFile file,
            String targetPid, String proxyServer) {
        WebClient webClient = WebClient.create(proxyServer);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", file.getResource());
        multipartBodyBuilder.part("targetPid", targetPid);
        return webClient.post()
                .uri("/hotfix")
                .accept(MediaType.APPLICATION_JSON)
                .syncBody(multipartBodyBuilder.build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Result<HotfixResult>>(){});
    }
}
