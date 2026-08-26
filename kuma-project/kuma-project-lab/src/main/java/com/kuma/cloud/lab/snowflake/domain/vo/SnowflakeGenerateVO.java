package com.kuma.cloud.lab.snowflake.domain.vo;

import com.kuma.cloud.lab.snowflake.support.SnowflakeIdParser.ParsedSnowflakeId;
import java.util.List;

/**
 * 雪花 ID 批量生成结果。
 */
public record SnowflakeGenerateVO(
        int count,
        List<ParsedSnowflakeId> ids
) {
}
