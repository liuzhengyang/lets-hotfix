package com.github.lzy.hotfix.log;

/**
 * @author liuzhengyang
 * Created on 2019-08-15
 */
public class LoggerFactory {
    public static Logger getLogger(Class<?> clazz) {
        return new DefaultLogger(Level.INFO, clazz);
    }
}
