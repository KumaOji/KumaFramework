/*
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.core.monitor;

import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.client.MonitorService;
import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.common.DateTimeUtil;
import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.entity.MonitorBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorServiceImpl
implements MonitorService {
    private Map<String, MonitorBean> map = new ConcurrentHashMap<String, MonitorBean>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Logger logger = LoggerFactory.getLogger(MonitorService.class);

    private void initMap(String time) {
        if (!this.map.containsKey(time)) {
            this.lock.writeLock().lock();
            try {
                if (!this.map.containsKey(time)) {
                    this.map.put(time, new MonitorBean(DateTimeUtil.parse(time)));
                }
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void save(MonitorBean monitorBean) {
        this.initMap(monitorBean.getDateTime());
        this.lock.readLock().lock();
        try {
            MonitorBean bean = this.map.get(monitorBean.getDateTime());
            bean.setApp(monitorBean.getApp());
            bean.setId(monitorBean.getId());
            bean.setName(monitorBean.getName());
            bean.setPre(monitorBean.getPre() + bean.getPre());
            bean.setAfter(monitorBean.getAfter() + bean.getAfter());
            bean.setMonitor(monitorBean.getMonitor());
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<MonitorBean> getAndDelete() {
        HashMap<String, MonitorBean> beanHashMap = new HashMap<String, MonitorBean>(this.map);
        ArrayList<MonitorBean> list = new ArrayList<MonitorBean>(beanHashMap.values());
        list.sort(null);
        if (list.size() > 1) {
            list.remove(list.size() - 1);
        }
        for (MonitorBean monitorBean : list) {
            this.lock.writeLock().lock();
            try {
                this.map.remove(monitorBean.getDateTime());
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
        return list;
    }
}

