package com.github.lzy.hotfix.log;

/**
 * @author liuzhengyang <liuzhengyang75@gmail.com>
 * Created on 2019-08-15
 */
public class LoggerFactory {
    public static Logger getLogger(Class<?> clazz) {
        return new DefaultLogger(Level.INFO, clazz);
    }
}
