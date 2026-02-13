/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.common.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name="BaseQuery", description="\u901a\u7528\u57fa\u7840\u67e5\u8be2Query")
public record BaseQuery(@Schema(description="\u76f8\u540c\u67e5\u8be2\u53c2\u6570") List<EqDTO> eqQuery, @Schema(description="\u65f6\u95f4\u8303\u56f4\u67e5\u8be2\u53c2\u6570") List<DateTimeBetweenDTO> dateTimeBetweenQuery, @Schema(description="\u6392\u5e8f\u67e5\u8be2\u53c2\u6570") List<SortDTO> sortQuery, @Schema(description="execl\u67e5\u8be2\u53c2\u6570") ExeclDTO execlQuery, @Schema(description="\u6a21\u7cca\u67e5\u8be2\u53c2\u6570") List<LikeDTO> likeQuery, @Schema(description="\u5305\u542b\u67e5\u8be2\u53c2\u6570") List<InDTO> inQuery, @Schema(description="\u4e0d\u5305\u542b\u67e5\u8be2\u53c2\u6570") List<NotInDTO> notInQuery) implements Serializable
{
    static final long serialVersionUID = -2483306509077581330L;

    @Schema(name="ExeclDTO", description="ExeclDTO")
    public record ExeclDTO(@Schema(description="\u4e0b\u8f7d\u6587\u4ef6\u540d\u79f0") String fileName, @Schema(description="\u6807\u9898") String title, @Schema(description="\u7c7b\u578b,\u9ed8\u8ba4HSSF", allowableValues={"HSSF,XSSF"}, example="createTime") String type, @Schema(description="sheetName") String sheetName) {
    }

    @Schema(name="DateTimeBetweenDTO", description="\u65f6\u95f4\u8303\u56f4DTO")
    public record DateTimeBetweenDTO(@Schema(description="\u5b57\u6bb5\u540d\u79f0") String filed, @Schema(description="\u5f00\u59cb\u65f6\u95f4 \u65f6\u95f4\u683c\u5f0f:yyyy-MM-dd HH:mm:ss") LocalDateTime startTime, @Schema(description="\u7ed3\u675f\u65f6\u95f4 \u65f6\u95f4\u683c\u5f0f:yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
    }

    @Schema(name="SortDTO", description="\u6392\u5e8fDTO")
    public record SortDTO(@Schema(description="\u6392\u5e8f\u5b57\u6bb5\u540d\u79f0,\u9ed8\u8ba4createTime", allowableValues={"id,createTime,updateTime"}, example="createTime") String filed, @Schema(description="\u6392\u5e8f\u89c4\u5219, \u9ed8\u8ba4desc", allowableValues={"desc,asc"}, example="desc") String order) {
    }

    @Schema(name="LikeDTO", description="\u6a21\u7cca\u67e5\u8be2DTO")
    public record LikeDTO(@Schema(description="\u5b57\u6bb5\u540d\u79f0") String filed, @Schema(description="\u5b57\u6bb5\u503c") String value) {
    }

    @Schema(name="EqDTO", description="\u76f8\u540c\u53c2\u6570\u67e5\u8be2DTO")
    public record EqDTO(@Schema(description="\u5b57\u6bb5\u540d\u79f0") String filed, @Schema(description="\u5b57\u6bb5\u503c") Object value) {
    }

    @Schema(name="InDTO", description="\u5305\u542b\u67e5\u8be2DTO")
    public record InDTO(@Schema(description="\u5b57\u6bb5\u540d\u79f0") String filed, @Schema(description="\u5b57\u6bb5\u503c") Object[] values) {
    }

    @Schema(name="NotInDTO", description="\u4e0d\u5305\u542b\u67e5\u8be2DTO")
    public record NotInDTO(@Schema(description="\u5b57\u6bb5\u540d\u79f0") String filed, @Schema(description="\u5b57\u6bb5\u503c") Object[] values) {
    }
}

