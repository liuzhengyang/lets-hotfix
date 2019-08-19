package com.github.lzy.hotfix.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.lzy.hotfix.model.Result;

/**
 * @author liuzhengyang
 */
@ControllerAdvice
public class ControllerExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Object> exception(Exception exception) {
        logger.info("Found exception ", exception);
        Result<Object> result = new Result<>();
        result.setCode(1);
        result.setMsg(exception.getMessage());
        return result;
    }
}
