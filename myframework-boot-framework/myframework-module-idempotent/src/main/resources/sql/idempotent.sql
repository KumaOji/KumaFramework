CREATE TABLE `business_idempotent` (
  `idempotent_id` bigint NOT NULL AUTO_INCREMENT COMMENT '幂等主键',
  `namespace` varchar(32) DEFAULT NULL COMMENT '命名空间',
  `source` varchar(64) DEFAULT NULL COMMENT '来源',
  `operation_type` varchar(64) DEFAULT NULL COMMENT '操作类型',
  `business_key` varchar(128) NOT NULL COMMENT '业务key',
  `unique_key` varchar(255) NOT NULL COMMENT '唯一键（用于幂等控制）',
  `idempotent_status` tinyint NOT NULL COMMENT '幂等状态(1为处理中，2为处理成功)',
  `object_version_number` bigint NOT NULL COMMENT '版本号',
  `response` blob COMMENT '响应数据',
  `create_date` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_modified_date` datetime NOT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`idempotent_id`),
  UNIQUE KEY `udx_unique_key` (`unique_key`) USING BTREE COMMENT '幂等键唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务幂等表';
