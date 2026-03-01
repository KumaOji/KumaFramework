/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.rocketmq.tags;

public enum GoodsTagsEnum {
    GENERATOR_GOODS_INDEX("\u751f\u6210\u5546\u54c1\u7d22\u5f15"),
    UPDATE_GOODS_INDEX("\u66f4\u65b0\u5546\u54c1\u7d22\u5f15"),
    UPDATE_GOODS_INDEX_PROMOTIONS("\u66f4\u65b0\u5546\u54c1\u7d22\u5f15\u4fc3\u9500\u4fe1\u606f"),
    DELETE_GOODS_INDEX_PROMOTIONS("\u66f4\u65b0\u5546\u54c1\u7d22\u5f15\u4fc3\u9500\u4fe1\u606f"),
    UPDATE_GOODS_INDEX_FIELD("\u66f4\u65b0\u5546\u54c1\u7d22\u5f15\u90e8\u5206\u5b57\u6bb5"),
    RESET_GOODS_INDEX("\u91cd\u7f6e\u5546\u54c1\u7d22\u5f15"),
    GOODS_DELETE("\u5220\u9664\u5546\u54c1"),
    GOODS_AUDIT("\u5ba1\u6838\u5546\u54c1"),
    GOODS_COLLECTION("\u6536\u85cf\u5546\u54c1"),
    BUY_GOODS_COMPLETE("\u8d2d\u4e70\u5546\u54c1\u5b8c\u6210"),
    SKU_DELETE("\u5220\u9664\u5546\u54c1SKU"),
    VIEW_GOODS("\u67e5\u770b\u5546\u54c1"),
    GOODS_COMMENT_COMPLETE("\u5546\u54c1\u8bc4\u4ef7");

    private final String description;

    private GoodsTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return this.description;
    }
}

