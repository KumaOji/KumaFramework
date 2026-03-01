/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

/**
 * 促销分类枚举
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-22 10:47:43
 */
public enum PromotionTypeEnum implements CommonEnum {

    /** 促销枚举 */
    PINTUAN(1, "拼团"),
    SECKILL(2, "秒杀"),
    COUPON(3, "优惠券"),
    PLATFORM_COUPON(4, "平台优惠券"),
    FULL_DISCOUNT(5, "满减"),
    POINTS_GOODS(6, "积分商品"),
    KANJIA(7, "砍价"),
    COUPON_ACTIVITY(8, "优惠券活动");

    /** 有促销库存的活动类型 */
    public static final PromotionTypeEnum[] HAVE_STOCK_PROMOTION =
            new PromotionTypeEnum[] {PINTUAN, SECKILL, KANJIA, POINTS_GOODS};

    private final int code;

    private final String description;

    PromotionTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /** 是否拥有库存 */
    public static boolean haveStock(String promotionType) {
        for (PromotionTypeEnum promotionTypeEnum : HAVE_STOCK_PROMOTION) {
            if (promotionTypeEnum.name().equals(promotionType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return description;
    }
}
