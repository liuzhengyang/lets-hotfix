package com.github.lzy.hotfix.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/4/21
 */
public class HotfixServiceTest {

    @Test
    public void testGetClassName() {
        String fileName = "Hello.java";
        System.out.println(fileName.substring(0, fileName.lastIndexOf(".")));
    }

}
