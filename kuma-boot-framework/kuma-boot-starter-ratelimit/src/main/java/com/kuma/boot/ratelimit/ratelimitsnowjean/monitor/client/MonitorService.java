package com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.client;

import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.entity.MonitorBean;

import java.util.List;

/**
 */
public interface MonitorService {

    /**
     * 获取并删除监控数据
     */
    List<MonitorBean> getAndDelete();

    /**
     * 保存一条监控数据
     */
    void save(MonitorBean monitorBean);

}
