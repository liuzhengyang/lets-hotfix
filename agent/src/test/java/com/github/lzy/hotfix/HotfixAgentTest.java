package com.github.lzy.hotfix;

import static org.junit.Assert.*;

import java.lang.instrument.Instrumentation;

import org.junit.Test;

import net.bytebuddy.agent.ByteBuddyAgent;

public class HotfixAgentTest {

    @Test
    public void findStaticInnerClass() throws Exception {
        DummyStaticInnerService dummyService = new DummyStaticInnerService();
        Instrumentation instrumentation = ByteBuddyAgent.install();
        Class<?> targetClass = HotfixAgent.findTargetClass("com.github.lzy.hotfix.HotfixAgentTest$DummyStaticInnerService", instrumentation);
        assertNotNull(targetClass);
        assertEquals(targetClass, dummyService.getClass());
    }

    @Test
    public void findStaticClass() {
        DummyStaticOuterService dummyService = new DummyStaticOuterService();
        Instrumentation instrumentation = ByteBuddyAgent.install();
        Class<?> targetClass = HotfixAgent.findTargetClass("com.github.lzy.hotfix.DummyStaticOuterService", instrumentation);
        assertNotNull(targetClass);
        assertEquals(targetClass, dummyService.getClass());
    }

    static class DummyStaticInnerService {
        public String foo() {
            return "foo";
        }
    }
}

class DummyStaticOuterService {
    public String foo() {
        return "foo";
    }
}