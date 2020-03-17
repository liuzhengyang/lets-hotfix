package com.github.lzy.hotfix.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liuzhengyang
 * Created on 2019-08-15
 */
public class DefaultLogger implements Logger {

    private Level level;
    private Class<?> clazz;
    private DateTimeFormatter dateTimeFormatter;
    private static final String TIME_FORMATTER_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public DefaultLogger(Level level, Class<?> clazz) {
        this.level = level;
        this.clazz = clazz;
        dateTimeFormatter = DateTimeFormatter.ofPattern(TIME_FORMATTER_PATTERN);
    }

    @Override
    public void info(String message) {
        System.out.println(String.format("%s [%s] %s: %s\n", dateTimeFormatter.format(LocalDateTime.now()), Level.INFO, clazz.getCanonicalName(), message));
    }

    @Override
    public void error(String message) {
        System.err.println(String.format("%s [%s] %s: %s\n", dateTimeFormatter.format(LocalDateTime.now()), Level.ERROR, clazz.getCanonicalName(), message));
    }
}
