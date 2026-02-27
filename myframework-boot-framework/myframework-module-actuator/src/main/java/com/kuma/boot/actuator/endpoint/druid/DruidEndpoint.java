
package com.kuma.boot.actuator.endpoint.druid;

import com.alibaba.druid.stat.JdbcStatManager;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import javax.management.JMException;
import javax.management.openmbean.TabularData;

/**
 * druid stat 端点
 */
@Endpoint(id = "kmcdruidendpoint")
public class DruidEndpoint {

    @ReadOperation
    public TabularData jdbcStat() throws JMException {
        JdbcStatManager instance = JdbcStatManager.getInstance();
        return instance.getSqlList();
    }

}
