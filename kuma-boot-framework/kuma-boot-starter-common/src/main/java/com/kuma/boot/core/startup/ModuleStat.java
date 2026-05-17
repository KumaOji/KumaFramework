package com.kuma.boot.core.startup;

public class ModuleStat extends ChildrenStat<BeanStat> {

    private String threadName;

    public String getThreadName() { return threadName; }
    public void setThreadName(String threadName) { this.threadName = threadName; }
}
