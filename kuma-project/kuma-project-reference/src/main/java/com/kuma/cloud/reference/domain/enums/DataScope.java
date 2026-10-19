package com.kuma.cloud.reference.domain.enums;

/** 只允许固定路由，不接受客户端输入任意数据源名或 search_path。 */
public enum DataScope {
    MAIN("referenceMain", "main", "kuma_reference_main"),
    MYSQL("referenceMysql", "mysql", "kuma_reference_mysql");

    private final String dataSource;
    private final String tenant;
    private final String schema;

    DataScope(String dataSource, String tenant, String schema) {
        this.dataSource = dataSource;
        this.tenant = tenant;
        this.schema = schema;
    }
    public String dataSource() { return dataSource; }
    public String tenant() { return tenant; }
    public String schema() { return schema; }
}
