package com.github.lzy.hotreload;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.hotreload.dynamiccompiler.DynamicCompiler;

/**
 * @author liuzhengyang
 * 2021/1/4
 */
public class HotReloadWorker {
    private static final Logger logger = LoggerFactory.getLogger(HotReloadWorker.class);

    // FIXME multi classes of different classloader ?
    private static Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    public static void doReload(Instrumentation instrumentation, String[] splits)
            throws IOException, ClassNotFoundException, UnmodifiableClassException {
        logger.info("Start reloading. Current classloader is " + HotReloadWorker.class.getClassLoader());
        String className = splits[0];
        String replaceTargetFile = splits[1];
        if (replaceTargetFile == null) {
            logger.error("Invalid argument file is null");
            return;
        }
        File file = Paths.get(replaceTargetFile).toFile();
        if (replaceTargetFile.endsWith(".class")) {
            logger.info("Reload by class file");
            byte[] newClazzByteCode = Files.readAllBytes(file.toPath());
            doReloadClassFile(instrumentation, className, newClazzByteCode);
        } else {
            logger.info("Reload by java file");
            byte[] newClazzSourceBytes = Files.readAllBytes(file.toPath());
            doCompileThenReloadClassFile(instrumentation, className, new String(newClazzSourceBytes, UTF_8));
        }
    }

    private static void doCompileThenReloadClassFile(Instrumentation instrumentation, String className,
                                                     String sourceCode) {
        ClassLoader classLoader = getClassLoader(className, instrumentation);
        logger.info("Target class {} class loader {}", className, classLoader);
        DynamicCompiler dynamicCompiler = new DynamicCompiler(classLoader);
        dynamicCompiler.addSource(className, sourceCode);
        Map<String, byte[]> classNameToByteCodeMap = dynamicCompiler.buildByteCodes();
        classNameToByteCodeMap.forEach((clazzName, bytes) -> {
            try {
                Files.write(Paths.get("/tmp/replace_" + clazzName), bytes);
                doReloadClassFile(instrumentation, clazzName, bytes);
            } catch (Exception e) {
                logger.error("Class " + clazzName + " reload error ");
                e.printStackTrace();
            }
        });
    }

    private static void doReloadClassFile(Instrumentation instrumentation, String className,
                                          byte[] newClazzByteCode) throws UnmodifiableClassException, ClassNotFoundException {
        Class<?> clazz = getToReloadClass(instrumentation, className, newClazzByteCode);
        if (clazz == null) {
            logger.error("Class " + className + " not found");
        } else {
            instrumentation.redefineClasses(new ClassDefinition(clazz, newClazzByteCode));
            logger.info("Congratulations! Reload " + clazz + " success!");
        }
    }

    private static Class<?> getToReloadClass(Instrumentation instrumentation, String className,
                                             byte[] newClazzByteCode) {
        Class<?> clazz = findTargetClass(className, instrumentation);
        if (clazz == null) {
            clazz = defineNewClass(className, newClazzByteCode, clazz);
        }
        return clazz;
    }

    private static Class<?> defineNewClass(String className, byte[] newClazzByteCode, Class<?> clazz) {
        logger.info("Class " + className + " not found, try to define a new class");
        ClassLoader classLoader = HotReloadWorker.class.getClassLoader();
        try {
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class,
                    byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            clazz = (Class<?>) defineClass.invoke(classLoader, className, newClazzByteCode
                    , 0, newClazzByteCode.length);
            logger.info("Class " + className + " define success " + clazz);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    private static ClassLoader getClassLoader(String className, Instrumentation instrumentation) {
        Class<?> targetClass = findTargetClass(className, instrumentation);
        if (targetClass != null) {
            return targetClass.getClassLoader();
        }
        return HotReloadWorker.class.getClassLoader();
    }

    //@VisibleForTest
    static Class<?> findTargetClass(String className, Instrumentation instrumentation) {
        return classCache.computeIfAbsent(className, clazzName -> {
            Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
            return Arrays.stream(allLoadedClasses)
                    .parallel()
                    .filter(clazz -> clazzName.equals(clazz.getName()))
                    .findFirst()
                    .orElse(null);
        });
    }
}
