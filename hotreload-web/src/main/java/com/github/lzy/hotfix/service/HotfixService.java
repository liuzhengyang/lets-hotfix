package com.github.lzy.hotfix.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.liuzhengyang.hotreload.bytecode.util.ClassByteCodeUtils;
import com.github.lzy.hotfix.model.JvmProcess;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * @author liuzhengyang
 */
@Service
public class HotfixService {
    private static final Logger logger = LoggerFactory.getLogger(HotfixService.class);

    @Value("${agent.path}")
    private String agentPath;

    public List<JvmProcess> getProcessList() {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        return list.stream()
                .map(JvmProcess::new)
                .collect(Collectors.toList());
    }

    public String hotfix(MultipartFile file, String targetPid) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("Invalid file name" + fileName);
        }
        logger.info("Start to reload {} in process id {}", fileName, targetPid);
        JvmProcess jvmProcess = findProcess(targetPid);
        if (jvmProcess == null) {
            logger.info("Target process {} not found", targetPid);
            throw new IllegalArgumentException("Target process not found " + targetPid);
        }
        VirtualMachine attach = VirtualMachine.attach(targetPid);
        String className = getClassName(file);
        Path replaceClassFile = Files.write(Paths.get("/tmp/" + className), file.getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        logger.info("Save replace class file to {}", replaceClassFile);
        String agentArgs = String.join(",", className,
                replaceClassFile.toFile().getAbsolutePath());
        try {
            attach.loadAgent(agentPath, agentArgs);
        } finally {
            attach.detach();
//            Files.delete(replaceClassFile);
            logger.info("Reload finished!");
        }
        return className;
    }

    String getClassName(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Invalid file name" + originalFilename);
        }
        if (originalFilename.endsWith(".class")) {
            return ClassByteCodeUtils.getClassNameFromByteCode(file.getBytes());
        }
        byte[] bytes = file.getBytes();
        String sourceCode = new String(bytes, StandardCharsets.UTF_8);
        return ClassByteCodeUtils.getClassNameFromSourceCode(sourceCode);
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
