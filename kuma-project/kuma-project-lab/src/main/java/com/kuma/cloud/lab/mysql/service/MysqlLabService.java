package com.kuma.cloud.lab.mysql.service;

import com.kuma.cloud.lab.mysql.domain.vo.MysqlQueryResultVO;
import com.kuma.cloud.lab.mysql.domain.vo.MysqlScenarioVO;
import com.kuma.cloud.lab.mysql.domain.vo.MysqlTopicVO;

import java.util.List;

public interface MysqlLabService {

    List<MysqlTopicVO> syntax();

    MysqlScenarioVO runScenario();

    MysqlQueryResultVO createTable();

    MysqlQueryResultVO addColumn();

    MysqlQueryResultVO groupBy();

    MysqlQueryResultVO window();
}
