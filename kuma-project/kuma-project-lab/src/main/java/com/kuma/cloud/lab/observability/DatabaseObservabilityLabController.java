package com.kuma.cloud.lab.observability;

import com.kuma.boot.common.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "OTel 与 SkyWalking 真实数据库实验")
@RestController
@RequestMapping("/lab/observability/database")
public class DatabaseObservabilityLabController {
    private final DataSource dataSource;
    private final ObservabilityDatabaseProperties properties;

    public DatabaseObservabilityLabController(DataSource dataSource, ObservabilityDatabaseProperties properties) {
        this.dataSource = dataSource;
        this.properties = properties;
    }

    @Operation(summary = "真实数据库 + OTel：正常提交、数据库慢查询、重复键异常与回滚验证")
    @PostMapping("/otel")
    public Result<List<DatabaseObservabilityLearningDemo.Report>> otel() throws SQLException {
        return Result.success(scenarios(false));
    }

    @Trace(operationName = "lab.database.scenario")
    @Operation(summary = "真实数据库 + SkyWalking Agent：JDBC 调用、慢 SQL 与事务回滚")
    @PostMapping("/skywalking")
    public Result<List<DatabaseObservabilityLearningDemo.Report>> skywalking() throws SQLException {
        if (!DatabaseObservabilityLearningDemo.validNativeId(TraceContext.traceId())) {
            throw new IllegalStateException("Attach the SkyWalking Agent to LabApplication before running native JDBC experiments");
        }
        return Result.success(scenarios(true));
    }

    private List<DatabaseObservabilityLearningDemo.Report> scenarios(boolean nativeMode) throws SQLException {
        var connections = connections();
        List<DatabaseObservabilityLearningDemo.Report> reports = new ArrayList<>();
        for (String scenario : List.of("normal", "slow", "error")) reports.add(DatabaseObservabilityLearningDemo.run(
                connections, scenario, nativeMode, properties.getExportEndpoint()));
        return reports;
    }

    private DatabaseObservabilityLearningDemo.Connections connections() {
        if (properties.getJdbcUrl() == null || properties.getJdbcUrl().isBlank()) return dataSource::getConnection;
        return DatabaseObservabilityLearningDemo.fromSettings(properties.getJdbcUrl(), properties.getUser(), properties.getPassword());
    }
}
