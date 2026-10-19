package com.kuma.cloud.reference;

import org.junit.jupiter.api.Tag;
import org.springframework.test.context.ActiveProfiles;

/** 同一套端到端契约在真实 WSL PostgreSQL 上重复验证。 */
@Tag("wsl")
@ActiveProfiles(value = "wsl", inheritProfiles = false)
class WslReferenceApplicationTest extends ReferenceApplicationTest {}
