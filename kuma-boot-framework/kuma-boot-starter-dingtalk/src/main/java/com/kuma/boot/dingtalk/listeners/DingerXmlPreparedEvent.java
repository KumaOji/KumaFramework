/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.SpringBootVersion
 *  org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
 *  org.springframework.context.ApplicationListener
 */
package com.kuma.boot.dingtalk.listeners;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.annatations.DingerScan;
import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

@Deprecated
public class DingerXmlPreparedEvent
implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        LogUtils.info((String)"ready to execute dinger analysis.", (Object[])new Object[0]);
        this.loadPrimarySources(event);
    }

    private void loadPrimarySources(ApplicationEnvironmentPreparedEvent event) {
        Set allSources = SpringBootVersion.getVersion().startsWith("1.") ? event.getSpringApplication().getSources() : event.getSpringApplication().getAllSources();
        HashSet<Class> primarySources = new HashSet<Class>();
        for (Object source : allSources) {
            Class clazz;
            if (!Class.class.isInstance(source) || !(clazz = (Class)source).isAnnotationPresent(DingerScan.class)) continue;
            primarySources.add(clazz);
        }
    }
}

