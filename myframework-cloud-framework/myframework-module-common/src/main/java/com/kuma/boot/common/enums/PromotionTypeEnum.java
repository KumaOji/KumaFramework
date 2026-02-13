/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum PromotionTypeEnum implements CommonEnum
{
    PINTUAN(1, "\u62fc\u56e2"),
    SECKILL(2, "\u79d2\u6740"),
    COUPON(3, "\u4f18\u60e0\u5238"),
    PLATFORM_COUPON(4, "\u5e73\u53f0\u4f18\u60e0\u5238"),
    FULL_DISCOUNT(5, "\u6ee1\u51cf"),
    POINTS_GOODS(6, "\u79ef\u5206\u5546\u54c1"),
    KANJIA(7, "\u780d\u4ef7"),
    COUPON_ACTIVITY(8, "\u4f18\u60e0\u5238\u6d3b\u52a8");

    public static final PromotionTypeEnum[] HAVE_STOCK_PROMOTION;
    private final int code;
    private final String description;

    private PromotionTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static boolean haveStock(String promotionType) {
        for (PromotionTypeEnum promotionTypeEnum : HAVE_STOCK_PROMOTION) {
            if (!promotionTypeEnum.name().equals(promotionType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.description;
    }

    static {
        HAVE_STOCK_PROMOTION = new PromotionTypeEnum[]{PINTUAN, SECKILL, KANJIA, POINTS_GOODS};
    }
}

