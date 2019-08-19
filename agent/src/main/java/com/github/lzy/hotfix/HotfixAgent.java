package com.github.lzy.hotfix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.lzy.hotfix.log.Logger;
import com.github.lzy.hotfix.log.LoggerFactory;

/**
 * @author liuzhengyang
 */
public class HotfixAgent {

    private static final Logger logger = LoggerFactory.getLogger(HotfixAgent.class);

    // FIXME multi classes of different classloader ?
    private static Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    public static void agentmain(String agentArgs, Instrumentation instrumentation)
            throws IOException, UnmodifiableClassException, ClassNotFoundException {
        if (agentArgs == null) {
            throw new IllegalArgumentException("Agent args is null");
        }
        String[] splits = agentArgs.split(",");
        if (splits.length < 2) {
            throw new IllegalArgumentException(agentArgs);
        }
        logger.info("Start reloading. Current classloader is " + HotfixAgent.class.getClassLoader());
        doReload(instrumentation, splits);
    }

    private static void doReload(Instrumentation instrumentation, String[] splits)
            throws IOException, ClassNotFoundException, UnmodifiableClassException {
        String className = splits[0];
        String replaceTargetClassFile = splits[1];
        File file = Paths.get(replaceTargetClassFile).toFile();
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] newClazzByteCode = new byte[inputStream.available()];
            inputStream.read(newClazzByteCode);
            Class<?> clazz = getToReloadClass(instrumentation, className, newClazzByteCode);
            if (clazz == null) {
                logger.error("Class " + className + " not found");
            } else {
                instrumentation.redefineClasses(new ClassDefinition(clazz, newClazzByteCode));
                logger.info("Congratulations! Reload " + clazz + " success!");
            }
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
        ClassLoader classLoader = HotfixAgent.class.getClassLoader();
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
