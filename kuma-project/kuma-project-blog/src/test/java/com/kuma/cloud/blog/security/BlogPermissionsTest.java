package com.kuma.cloud.blog.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BlogPermissionsTest {

    @Test
    void catalogIncludesKnowledgeBasePermissions() {
        assertThat(BlogPermissions.catalog())
                .extracting(BlogPermissions.PermissionDef::code)
                .contains(
                        BlogPermissions.AI_CHAT_INGEST,
                        BlogPermissions.AI_CHAT_RAG,
                        BlogPermissions.AI_CHAT_ALL);
        assertThat(BlogPermissions.catalog())
                .filteredOn(def -> "ai_chat".equals(def.module()))
                .extracting(BlogPermissions.PermissionDef::name)
                .contains("知识库入库", "知识库问答", "AI对话全部权限");
    }
}
