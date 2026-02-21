/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.druid.stat.JdbcStatManager
 *  org.springframework.boot.actuate.endpoint.annotation.Endpoint
 *  org.springframework.boot.actuate.endpoint.annotation.ReadOperation
 */
package com.kuma.boot.actuator.endpoint.druid;

import com.alibaba.druid.stat.JdbcStatManager;
import javax.management.JMException;
import javax.management.openmbean.TabularData;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id="kmcdruidendpoint")
public class DruidEndpoint {
    @ReadOperation
    public TabularData jdbcStat() throws JMException {
        JdbcStatManager instance = JdbcStatManager.getInstance();
        return instance.getSqlList();
    }
}

