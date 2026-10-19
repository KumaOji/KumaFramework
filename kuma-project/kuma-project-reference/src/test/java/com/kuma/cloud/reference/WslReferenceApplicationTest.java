package com.kuma.cloud.reference;

import org.junit.jupiter.api.Tag;
import org.springframework.test.context.ActiveProfiles;

/** WSL 环境会启动 PostgreSQL 和 MySQL 数据源，再验证 PostgreSQL 路由契约。 */
@Tag("wsl")
@ActiveProfiles(value = "wsl", inheritProfiles = false)
class WslReferenceApplicationTest extends ReferenceApplicationTest {}
