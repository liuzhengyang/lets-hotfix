package com.github.lzy.hotreload;

import static org.junit.Assert.*;

import java.lang.instrument.Instrumentation;

import org.junit.Test;

import net.bytebuddy.agent.ByteBuddyAgent;

public class HotfixAgentTest {

    @Test
    public void loop() throws InterruptedException {
        for (int i = 0; i < 1000000; i++) {
            Thread.sleep(1000);
            invoke();
        }
    }

    void invoke() {
        System.out.println("lzy");
    }

    @Test
    public void findStaticInnerClass() throws Exception {
        DummyStaticInnerService dummyService = new DummyStaticInnerService();
        Instrumentation instrumentation = ByteBuddyAgent.install();
        Class<?> targetClass = HotReloadWorker.findTargetClass("com.github.lzy.hotreload.HotfixAgentTest$DummyStaticInnerService", instrumentation);
        assertNotNull(targetClass);
        assertEquals(targetClass, dummyService.getClass());
    }

    @Test
    public void findStaticClass() {
        DummyStaticOuterService dummyService = new DummyStaticOuterService();
        Instrumentation instrumentation = ByteBuddyAgent.install();
        Class<?> targetClass = HotReloadWorker.findTargetClass("com.github.lzy.hotreload.DummyStaticOuterService", instrumentation);
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
