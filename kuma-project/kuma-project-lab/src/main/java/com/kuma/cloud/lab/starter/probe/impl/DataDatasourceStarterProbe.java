package com.kuma.cloud.lab.starter.probe.impl;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeStepVO;
import com.kuma.cloud.lab.starter.probe.AbstractStarterProbe;
import com.kuma.cloud.lab.starter.support.StarterProbeStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@Component
public class DataDatasourceStarterProbe extends AbstractStarterProbe {

    public DataDatasourceStarterProbe() {
        super(
                StarterNameConstants.DATA_DATASOURCE_STARTER,
                "com.kuma.boot.data.datasource.autoconfigure.KmcDataSourceAutoConfiguration",
                "动态数据源与事务支持"
        );
    }

    @Override
    protected StarterProbeResultVO doDiagnose(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        boolean dataSourceReady = hasBean(applicationContext, DataSource.class);
        steps.add(step("bean.dataSource", dataSourceReady,
                dataSourceReady ? "DataSource Bean 已注册" : "DataSource Bean 缺失"));
        return result(
                dataSourceReady ? StarterProbeStatus.READY : StarterProbeStatus.FAILED,
                dataSourceReady ? "DataSource Starter 已就绪" : "DataSource Starter Bean 未就绪",
                steps,
                detailsOf("dataSourceBeans", applicationContext.getBeanNamesForType(DataSource.class).length)
        );
    }

    @Override
    protected StarterProbeResultVO doSmokeTest(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        if (!hasBean(applicationContext, DataSource.class)) {
            steps.add(step("jdbc.ping", false, "DataSource 不可用"));
            return result(StarterProbeStatus.SKIPPED, "未配置数据源，跳过 JDBC 冒烟测试", steps, detailsOf());
        }
        try {
            DataSource dataSource = requireBean(applicationContext, DataSource.class, "DataSource");
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT 1")) {
                boolean success = resultSet.next() && resultSet.getInt(1) == 1;
                steps.add(step("jdbc.ping", success, success ? "SELECT 1 成功" : "SELECT 1 返回异常"));
                return result(
                        success ? StarterProbeStatus.PASSED : StarterProbeStatus.FAILED,
                        success ? "DataSource Starter 冒烟测试通过" : "DataSource Starter 冒烟测试失败",
                        steps,
                        detailsOf("catalog", connection.getCatalog())
                );
            }
        } catch (Exception error) {
            steps.add(step("jdbc.ping", false, error.getMessage()));
            return result(
                    StarterProbeStatus.SKIPPED,
                    "数据源未连通，跳过 JDBC 冒烟测试: " + error.getMessage(),
                    steps,
                    detailsOf()
            );
        }
    }

}
