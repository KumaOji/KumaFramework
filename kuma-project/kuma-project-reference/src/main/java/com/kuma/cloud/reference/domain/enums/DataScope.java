package com.kuma.cloud.reference.domain.enums;

/** 只允许固定路由，不接受客户端输入任意数据源名或 search_path。 */
public enum DataScope {
    MAIN("referenceMain", "kuma_reference_main"),
    SANDBOX("referenceSandbox", "kuma_reference_sandbox");

    private final String dataSource;
    private final String schema;

    DataScope(String dataSource, String schema) { this.dataSource = dataSource; this.schema = schema; }
    public String dataSource() { return dataSource; }
    public String schema() { return schema; }
}
