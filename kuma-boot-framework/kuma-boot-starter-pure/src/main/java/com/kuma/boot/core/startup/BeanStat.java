package com.kuma.boot.core.startup;

public class BeanStat extends ChildrenStat<BeanStat> {

    private String type;

    private long realRefreshElapsedTime;

    @Deprecated
    private String beanClassName;

    @Deprecated
    private long beanRefreshStartTime;

    @Deprecated
    private long beanRefreshEndTime;

    @Deprecated
    private long refreshElapsedTime;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getRealRefreshElapsedTime() { return realRefreshElapsedTime; }
    public void setRealRefreshElapsedTime(long realRefreshElapsedTime) { this.realRefreshElapsedTime = realRefreshElapsedTime; }

    @Deprecated public String getBeanClassName() { return beanClassName; }
    @Deprecated public void setBeanClassName(String beanClassName) { this.beanClassName = beanClassName; }

    @Deprecated public long getRefreshElapsedTime() { return refreshElapsedTime; }
    @Deprecated public void setRefreshElapsedTime(long refreshElapsedTime) { this.refreshElapsedTime = refreshElapsedTime; }

    @Deprecated public long getBeanRefreshStartTime() { return beanRefreshStartTime; }
    @Deprecated public void setBeanRefreshStartTime(long beanRefreshStartTime) { this.beanRefreshStartTime = beanRefreshStartTime; }

    @Deprecated public long getBeanRefreshEndTime() { return beanRefreshEndTime; }
    @Deprecated public void setBeanRefreshEndTime(long beanRefreshEndTime) { this.beanRefreshEndTime = beanRefreshEndTime; }
}
