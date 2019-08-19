package com.github.lzy.hotfix.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.lzy.hotfix.model.JvmProcess;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

/**
 * @author liuzhengyang
 */
@Service
public class HotfixService {
    private static final Logger logger = LoggerFactory.getLogger(HotfixService.class);

    @Value("${agent.path}")
    private String agentPath;

    @Resource
    private MeterRegistry meterRegistry;

    public List<JvmProcess> getProcessList() {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        return list.stream()
                .map(JvmProcess::new)
                .collect(Collectors.toList());
    }

    public String hotfix(MultipartFile file, String targetPid) throws Exception {
        logger.info("Start to reload {} in process id {}", file.getOriginalFilename(), targetPid);
        JvmProcess jvmProcess = findProcess(targetPid);
        if (jvmProcess == null) {
            logger.info("Target process {} not found", targetPid);
            throw new IllegalArgumentException("Target process not found " + targetPid);
        }
        VirtualMachine attach = VirtualMachine.attach(targetPid);
        ClassReader classReader = new ClassReader(file.getBytes());
        String className = classReader.getClassName();
        String targetClass = className.replaceAll("/", ".");
        Path replaceClassFile = Files.write(Paths.get("/tmp/" + targetClass), file.getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        logger.info("Save replace class file to {}", replaceClassFile);
        String agentArgs = String.join(",", targetClass,
                replaceClassFile.toFile().getAbsolutePath());
        try {
            List<Tag> tags = new ArrayList<>();
            tags.add(Tag.of("pid", targetPid));
            tags.add(Tag.of("targetClass", targetClass));
            tags.add(Tag.of("displayName", jvmProcess.getDisplayName()));
            tags.add(Tag.of("args", jvmProcess.getDetailVmArgs()));
            meterRegistry.timer("hotfixReload", tags).recordCallable(() -> {
                attach.loadAgent(agentPath, agentArgs);
                return "";
            });
            meterRegistry.counter("hotfixReload.success", tags).increment();
        } finally {
            attach.detach();
            Files.delete(replaceClassFile);
            logger.info("Reload finished!");
        }
        return targetClass;
    }

    private JvmProcess findProcess(String pid) {
        if (pid == null) {
            return null;
        }
        return getProcessList().stream()
                .filter(jvmProcess -> pid.equals(jvmProcess.getPid()))
                .findFirst()
                .orElse(null);
    }

}
