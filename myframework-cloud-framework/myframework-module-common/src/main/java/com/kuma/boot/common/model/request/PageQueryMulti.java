/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  io.swagger.v3.oas.annotations.media.Schema
 *  io.swagger.v3.oas.annotations.media.Schema$RequiredMode
 *  jakarta.validation.constraints.Max
 *  jakarta.validation.constraints.Min
 *  jakarta.validation.constraints.NotNull
 */
package com.kuma.boot.common.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name="PageQuery", description="\u901a\u7528\u5206\u9875\u67e5\u8be2Query")
public class PageQueryMulti<QueryDTO>
implements Serializable {
    private static final long serialVersionUID = -2483306509077581330L;
    @Schema(description="\u5f53\u524d\u7b2c\u51e0\u9875\uff0c\u9ed8\u8ba41", example="1", requiredMode=Schema.RequiredMode.REQUIRED)
    @NotNull(message="\u5f53\u524d\u9875\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a")
    @Min(value=0L)
    @Max(value=0x7FFFFFFFL)
    private @NotNull(message="\u5f53\u524d\u9875\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a") @Min(value=0L) @Max(value=0x7FFFFFFFL) Integer currentPage = 1;
    @Schema(description="\u6bcf\u9875\u663e\u793a\u6761\u6570\uff0c\u9ed8\u8ba410", example="10", requiredMode=Schema.RequiredMode.REQUIRED)
    @NotNull(message="\u6bcf\u9875\u6570\u636e\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a")
    @Min(value=5L)
    @Max(value=100L)
    private @NotNull(message="\u6bcf\u9875\u6570\u636e\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a") @Min(value=5L) @Max(value=100L) Integer pageSize = 10;
    @Schema(description="\u67e5\u8be2\u53c2\u6570")
    private QueryDTO eqQuery;
    @Schema(description="\u65f6\u95f4\u8303\u56f4\u67e5\u8be2\u53c2\u6570")
    private List<DateTimeBetweenDTO> dateTimeBetweenQuery;
    @Schema(description="\u6392\u5e8f\u67e5\u8be2\u53c2\u6570")
    private List<SortDTO> sortQuery;
    @Schema(description="execl\u67e5\u8be2\u53c2\u6570")
    private ExeclDTO execlQuery;
    @Schema(description="\u6a21\u7cca\u67e5\u8be2\u53c2\u6570")
    private List<LikeDTO> likeQuery;
    @Schema(description="\u5305\u542b\u67e5\u8be2\u53c2\u6570")
    private List<InDTO> inQuery;
    @Schema(description="\u4e0d\u5305\u542b\u67e5\u8be2\u53c2\u6570")
    private List<NotInDTO> notInQuery;

    @JsonIgnore
    public long offset() {
        long current = this.currentPage.intValue();
        if (current <= 1L) {
            return 0L;
        }
        return (current - 1L) * (long)this.pageSize.intValue();
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public QueryDTO getEqQuery() {
        return this.eqQuery;
    }

    public void setEqQuery(QueryDTO eqQuery) {
        this.eqQuery = eqQuery;
    }

    public List<DateTimeBetweenDTO> getDateTimeBetweenQuery() {
        return this.dateTimeBetweenQuery;
    }

    public void setDateTimeBetweenQuery(List<DateTimeBetweenDTO> dateTimeBetweenQuery) {
        this.dateTimeBetweenQuery = dateTimeBetweenQuery;
    }

    public List<SortDTO> getSortQuery() {
        return this.sortQuery;
    }

    public void setSortQuery(List<SortDTO> sortQuery) {
        this.sortQuery = sortQuery;
    }

    public ExeclDTO getExeclQuery() {
        return this.execlQuery;
    }

    public void setExeclQuery(ExeclDTO execlQuery) {
        this.execlQuery = execlQuery;
    }

    public List<LikeDTO> getLikeQuery() {
        return this.likeQuery;
    }

    public void setLikeQuery(List<LikeDTO> likeQuery) {
        this.likeQuery = likeQuery;
    }

    public List<InDTO> getInQuery() {
        return this.inQuery;
    }

    public void setInQuery(List<InDTO> inQuery) {
        this.inQuery = inQuery;
    }

    public List<NotInDTO> getNotInQuery() {
        return this.notInQuery;
    }

    public void setNotInQuery(List<NotInDTO> notInQuery) {
        this.notInQuery = notInQuery;
    }

    @Schema(name="ExeclDTO", description="ExeclDTO")
    public static class ExeclDTO {
        @Schema(description="\u4e0b\u8f7d\u6587\u4ef6\u540d\u79f0")
        private String fileName = "\u4e34\u65f6\u6587\u4ef6";
        @Schema(description="\u6807\u9898")
        private String title = "title";
        @Schema(description="\u7c7b\u578b,\u9ed8\u8ba4HSSF", allowableValues={"HSSF,XSSF"}, example="createTime")
        private String type = "HSSF";
        @Schema(description="sheetName")
        private String sheetName = "sheetName";

        public String getFileName() {
            return this.fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSheetName() {
            return this.sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }
    }

    @Schema(name="DateTimeBetweenDTO", description="\u65f6\u95f4\u8303\u56f4DTO")
    public static class DateTimeBetweenDTO {
        @Schema(description="\u5b57\u6bb5\u540d\u79f0")
        private String filed;
        @Schema(description="\u5f00\u59cb\u65f6\u95f4 \u65f6\u95f4\u683c\u5f0f:yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;
        @Schema(description="\u7ed3\u675f\u65f6\u95f4 \u65f6\u95f4\u683c\u5f0f:yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;

        public String getFiled() {
            return this.filed;
        }

        public void setFiled(String filed) {
            this.filed = filed;
        }

        public LocalDateTime getStartTime() {
            return this.startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public LocalDateTime getEndTime() {
            return this.endTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }
    }

    @Schema(name="SortDTO", description="\u6392\u5e8fDTO")
    public static class SortDTO {
        @Schema(description="\u6392\u5e8f\u5b57\u6bb5\u540d\u79f0,\u9ed8\u8ba4createTime", allowableValues={"id,createTime,updateTime"}, example="createTime")
        private String filed = "createTime";
        @Schema(description="\u6392\u5e8f\u89c4\u5219, \u9ed8\u8ba4desc", allowableValues={"desc,asc"}, example="desc")
        private String order = "desc";

        public String getFiled() {
            return this.filed;
        }

        public void setFiled(String filed) {
            this.filed = filed;
        }

        public String getOrder() {
            return this.order;
        }

        public void setOrder(String order) {
            this.order = order;
        }
    }

    @Schema(name="LikeDTO", description="\u6a21\u7cca\u67e5\u8be2DTO")
    public static class LikeDTO {
        @Schema(description="\u5b57\u6bb5\u540d\u79f0")
        private String filed;
        @Schema(description="\u5b57\u6bb5\u503c")
        private String value;

        public String getFiled() {
            return this.filed;
        }

        public void setFiled(String filed) {
            this.filed = filed;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Schema(name="InDTO", description="\u5305\u542b\u67e5\u8be2DTO")
    public static class InDTO {
        @Schema(description="\u5b57\u6bb5\u540d\u79f0")
        private String filed;
        @Schema(description="\u5b57\u6bb5\u503c")
        private Object[] values;

        public String getFiled() {
            return this.filed;
        }

        public void setFiled(String filed) {
            this.filed = filed;
        }

        public Object[] getValues() {
            return this.values;
        }

        public void setValues(Object[] values) {
            this.values = values;
        }
    }

    @Schema(name="NotInDTO", description="\u4e0d\u5305\u542b\u67e5\u8be2DTO")
    public static class NotInDTO {
        @Schema(description="\u5b57\u6bb5\u540d\u79f0")
        private String filed;
        @Schema(description="\u5b57\u6bb5\u503c")
        private Object[] values;

        public String getFiled() {
            return this.filed;
        }

        public void setFiled(String filed) {
            this.filed = filed;
        }

        public Object[] getValues() {
            return this.values;
        }

        public void setValues(Object[] values) {
            this.values = values;
        }
    }
}

