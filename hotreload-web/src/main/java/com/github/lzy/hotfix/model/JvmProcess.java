package com.github.lzy.hotfix.model;

import static com.github.lzy.hotfix.util.VMUtils.getVmArgs;

import com.sun.tools.attach.VirtualMachineDescriptor;

import lombok.NoArgsConstructor;

/**
 * @author liuzhengyang
 */
@NoArgsConstructor
public class JvmProcess {
    private String pid;
    private String displayName;
    private String detailVmArgs;

    public JvmProcess(VirtualMachineDescriptor virtualMachineDescriptor) {
        this.pid = virtualMachineDescriptor.id();
        this.displayName = virtualMachineDescriptor.displayName();
        this.detailVmArgs = getVmArgs(pid);
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDetailVmArgs() {
        return detailVmArgs;
    }

    public void setDetailVmArgs(String detailVmArgs) {
        this.detailVmArgs = detailVmArgs;
    }
}
