package com.kuma.cloud.reference.controller;

import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.cloud.reference.support.datasource.JdbcSchemaInspector;
import com.kuma.cloud.reference.domain.vo.SchemaVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/datasources")
public class SchemaController {
    private final JdbcSchemaInspector inspector;
    public SchemaController(JdbcSchemaInspector inspector) { this.inspector = inspector; }

    @GetMapping("/current")
    public SchemaVO current(@RequestParam(defaultValue = "MAIN") DataScope scope) {
        return inspector.inspect(scope);
    }
}
