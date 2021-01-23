package com.github.lzy.hotreload;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author liuzhengyang
 * 2021/1/3
 */
public class HotReloadClassLoader extends URLClassLoader {

    public HotReloadClassLoader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader().getParent());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // 优先从parent (SystemClassLoader)里加载系统类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("sun.") || name.startsWith("java."))) {
            return super.loadClass(name, resolve);
        }
        try {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } catch (Exception e) {
            //
        }
        return super.loadClass(name, resolve);
    }
}
