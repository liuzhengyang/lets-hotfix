package com.github.lzy.hotfix.log;

import org.junit.jupiter.api.Test;

/**
 * @author liuzhengyang
 * Created on 2019-08-15
 */
class DefaultLoggerTest {

    @Test
    void info() {
        DefaultLogger defaultLogger = new DefaultLogger(Level.INFO, getClass());
        defaultLogger.info("Info message");
        defaultLogger.error("Error message");
    }

}