/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.utils.number;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money类
 */
public class Money implements Comparable<Money>, Serializable {

    /**
     * 币种最小单位
     */
    private long cent;

    /**
     * 币种
     */
    private Currency currency;

    private Money(long cent, Currency currency) {
        this.cent = cent;
        this.currency = currency;
    }

    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    /**
     * 创建Money对象
     * @param minorUnits
     * @param currencyCode
     * @return
     */
    public static Money of(long minorUnits, String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        Preconditions.checkNotNull(currency, "currency can not be null: " + currencyCode);

        return of(minorUnits, currency);
    }

    /**
     * 创建Money对象
     * @param minorUnits
     * @param currency
     * @return
     */
    public static Money of(long minorUnits, Currency currency) {
        Money money = new Money(0, currency);

        money.cent = minorUnits;

        return money;
    }

    /**
     * 创建Money对象
     * @param amount
     * @param currencyCode
     * @return
     */
    public static Money of(BigDecimal amount, String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        Preconditions.checkNotNull(currency, "currency can not be null: " + currencyCode);

        return of(amount, currency);
    }

    /**
     * 创建Money对象
     * @param amount
     * @param currency
     * @return
     */
    public static Money of(BigDecimal amount, Currency currency) {
        Money money = new Money(0, currency);

        money.cent = amount.movePointRight(currency.getDefaultFractionDigits()).longValue();

        return money;
    }

    /**
     * 获取金额数，单位为元 内部系统强制使用getAmount，不能使用getCents。 比如：人民币 1元，返回1，100分返回1。日元最小单位是1元，返回1
     * @return
     */
    public BigDecimal getAmount() {
        return BigDecimal.valueOf(cent, currency.getDefaultFractionDigits());
    }

    /**
     * 返回币种最小单位 内部系统强制使用getAmount，不能使用getCents，除非在和银行渠道对接时，需要使用getCent。
     * @return
     */
    public long getCent() {
        return cent;
    }

    /**
     * 返回元与分的位数，比如人民币，返回2，日元返回0
     */
    public final int getCentFactor() {
        return currency.getDefaultFractionDigits();
    }

    /**
     * 除法
     * @param value
     * @return
     */
    public Money divide(long value) {
        return divide(value, DEFAULT_ROUNDING);
    }

    /**
     * 除法
     * @param value
     * @return
     */
    public Money divide(BigDecimal value) {
        BigDecimal newCent = BigDecimal.valueOf(this.cent).divide(value, DEFAULT_ROUNDING);
        return Money.of(newCent.longValue(), this.currency);
    }

    /**
     * 除法
     * @param value
     * @return
     */
    public Money divide(BigDecimal value, RoundingMode roundingMode) {
        BigDecimal newCent = BigDecimal.valueOf(this.cent).divide(value, roundingMode);
        return Money.of(newCent.longValue(), this.currency);
    }

    /**
     * 除法
     * @param value
     * @param roundingMode
     * @return
     */
    public Money divide(long value, RoundingMode roundingMode) {
        BigDecimal newCent =
                BigDecimal.valueOf(this.cent).divide(BigDecimal.valueOf(value), roundingMode);
        return Money.of(newCent.longValue(), this.currency);
    }

    /**
     * 加法
     * @param value
     * @return
     */
    public Money add(Money value) {
        assertSameCurrency(value);
        return Money.of(this.cent + value.cent, this.currency);
    }

    /**
     * 减法
     * @param value
     * @return
     */
    public Money subtract(Money value) {
        assertSameCurrency(value);
        return Money.of(this.cent - value.cent, this.currency);
    }

    /**
     * 乘法
     * @param value
     * @return
     */
    public Money multiply(long value) {
        return Money.of(this.cent * value, this.currency);
    }

    /**
     * 乘法
     * @param value
     * @param roundingMode
     * @return
     */
    public Money multiply(BigDecimal value, RoundingMode roundingMode) {
        long newCent =
                BigDecimal.valueOf(this.cent).multiply(value).setScale(0, roundingMode).longValue();
        return Money.of(newCent, this.currency);
    }

    public Money multiply(BigDecimal value) {
        long newCent =
                BigDecimal.valueOf(this.cent)
                        .multiply(value)
                        .setScale(0, DEFAULT_ROUNDING)
                        .longValue();
        return Money.of(newCent, this.currency);
    }

    /**
     * 相同币种比较
     * @param value
     */
    private void assertSameCurrency(Money value) {
        if (!Objects.equals(this.currency, value.currency)) {
            throw new IllegalArgumentException("Money instances must have the same currency");
        }
    }

    /**
     * 对比
     * @param value the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Money value) {
        assertSameCurrency(value);
        return Long.compare(this.cent, value.cent);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Money value) {
            return this.cent == value.cent && Objects.equals(this.currency, value.currency);
        }
        return false;
    }
}
