package com.kuma.cloud.lab.binlog.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.binlog.domain.MysqlBinlogChangeEvent;
import com.kuma.cloud.lab.binlog.domain.MysqlBinlogMonitorStatus;
import com.kuma.cloud.lab.binlog.service.MysqlBinlogEventStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "MySQL Binlog monitor")
@RestController
@RequestMapping("/lab/binlog")
public class MysqlBinlogController {

    private final MysqlBinlogEventStore eventStore;

    public MysqlBinlogController(MysqlBinlogEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Operation(summary = "Get the embedded binlog monitor status")
    @GetMapping("/status")
    public Result<MysqlBinlogMonitorStatus> status() {
        return Result.success(eventStore.status());
    }

    @Operation(summary = "Get the newest committed row changes")
    @GetMapping("/events")
    public Result<List<MysqlBinlogChangeEvent>> events(
            @RequestParam(defaultValue = "100") int limit
    ) {
        return Result.success(eventStore.latest(limit));
    }
}
