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

package com.kuma.boot.common.utils.number;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money类用于表示不同币种的金额，支持高精度计算和多币种操作。 * 该类是不可变的（immutable），并且实现了Comparable和Serializable接口。
 */
public final class MoneyOther implements Comparable<MoneyOther>, Serializable {

    private static final long serialVersionUID = 1L;

    private final BigDecimal amount;

    private final Currency currency;

    /**
     * 私有构造函数，确保通过工厂方法创建实例。 * * @param amount 金额，单位为元 * @param currency 币种，非空
     */
    private MoneyOther(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null.");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null.");
        }
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * 静态工厂方法，通过传入Currency和BigDecimal金额创建Money实例。 * 默认使用RoundingMode.HALF_UP进行四舍五入。 *
     * * @param currency 币种 * @param amount 金额，单位为元 * @return 新的Money实例
     */
    public static MoneyOther of(Currency currency, BigDecimal amount) {
        return of(currency, amount, RoundingMode.HALF_UP);
    }

    /**
     * 静态工厂方法，通过传入Currency和BigDecimal金额创建Money实例。 * 允许指定RoundingMode进行四舍五入。 * * @param
     * currency 币种 *
     * @param amount 金额，单位为元 * @param roundingMode 四舍五入模式 * @return 新的Money实例
     */
    public static MoneyOther of(Currency currency, BigDecimal amount, RoundingMode roundingMode) {
        Objects.requireNonNull(currency, "Currency cannot be null.");
        Objects.requireNonNull(amount, "Amount cannot be null.");
        Objects.requireNonNull(roundingMode, "RoundingMode cannot be null.");
        BigDecimal scaledAmount =
                amount.setScale(currency.getDefaultFractionDigits(), roundingMode);
        return new MoneyOther(scaledAmount, currency);
    }

    /**
     * 加法操作，返回新的Money实例。 * 仅允许相同币种的加法操作。 * * @param other 加数 * @return 相加后的Money实例
     * * @throws IllegalArgumentException 如果币种不一致
     */
    public MoneyOther add(MoneyOther other) {
        validateSameCurrency(other);
        BigDecimal resultAmount = this.amount.add(other.amount);
        return new MoneyOther(resultAmount, this.currency);
    }

    /**
     * 减法操作，返回新的Money实例。 * 仅允许相同币种的减法操作。 * * @param other 减数 * @return 相减后的Money实例
     * * @throws IllegalArgumentException 如果币种不一致
     */
    public MoneyOther subtract(MoneyOther other) {
        validateSameCurrency(other);
        BigDecimal resultAmount = this.amount.subtract(other.amount);
        return new MoneyOther(resultAmount, this.currency);
    }

    /**
     * 乘法操作，使用默认舍入模式（RoundingMode.HALF_UP），返回新的Money实例。 * * @param multiplier 乘数 * @return
     * 乘法后的Money实例 * @throws ArithmeticException 如果需要进行舍入但无法进行 * @throws
     * IllegalArgumentException 如果multiplier为null
     */
    public MoneyOther multiply(BigDecimal multiplier) {
        return multiply(multiplier, RoundingMode.HALF_UP);
    }

    /**
     * 乘法操作，返回新的Money实例。 * * @param multiplier 乘数 * @param roundingMode 四舍五入模式 * @return
     * 乘法后的Money实例 * @throws ArithmeticException 如果需要进行舍入但没有指定舍入模式 * @throws
     * IllegalArgumentException 如果multiplier或roundingMode为null
     */
    public MoneyOther multiply(BigDecimal multiplier, RoundingMode roundingMode) {
        Objects.requireNonNull(multiplier, "Multiplier cannot be null.");
        Objects.requireNonNull(roundingMode, "RoundingMode cannot be null.");
        BigDecimal resultAmount =
                this.amount
                        .multiply(multiplier)
                        .setScale(currency.getDefaultFractionDigits(), roundingMode);
        return new MoneyOther(resultAmount, this.currency);
    }

    /**
     * 除法操作，返回新的Money实例。 * * @param divisor 除数 * @param scale 保留的小数位数 * @param
     * roundingMode 四舍五入模式 *
     * @return 除法后的Money实例 * @throws ArithmeticException 如果除数为零或无法精确表示 * @throws
     * IllegalArgumentException 如果divisor或roundingMode为null
     */
    public MoneyOther divide(BigDecimal divisor, int scale, RoundingMode roundingMode) {
        Objects.requireNonNull(divisor, "Divisor cannot be null.");
        Objects.requireNonNull(roundingMode, "RoundingMode cannot be null.");
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Division by zero.");
        }
        BigDecimal resultAmount =
                this.amount
                        .divide(divisor, scale, roundingMode)
                        .setScale(currency.getDefaultFractionDigits(), roundingMode);
        return new MoneyOther(resultAmount, this.currency);
    }

    /**
     * 比较大小，仅允许相同币种的比较。 * * @param other 要比较的Money对象 * @return 负数、零或正数，分别表示小于、等于或大于
     * * @throws IllegalArgumentException 如果币种不一致
     */
    @Override
    public int compareTo(MoneyOther other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    /**
     * 获取金额，单位为元。 * * @return 金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 获取最小单位金额，通过Currency.getDefaultFractionDigits和amount计算。 * 例如，人民币1元 = 100分，日元1元 = 1元。
     * * *
     * @return 最小单位金额
     */
    public BigDecimal getAmountMinorUnit() {
        int fractionDigits = currency.getDefaultFractionDigits();
        return amount.movePointRight(fractionDigits);
    }

    /**
     * 获取币种。 * * @return 币种
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * 校验两个Money对象的币种是否相同。 * * @param other 另一个Money对象 * @throws IllegalArgumentException
     * 如果币种不一致
     */
    private void validateSameCurrency(MoneyOther other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currencies do not match ");
        }
    }

    /**
     * 重写equals方法，基于金额和币种判断相等。 * * @param o 其他对象 * @return 是否相等
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MoneyOther money = (MoneyOther) o;
        return amount.equals(money.amount) && currency.equals(money.currency);
    }

    /**
     * 重写hashCode方法，基于金额和币种生成哈希码。 * * @return 哈希码
     */
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    /**
     * 重写toString方法，格式化输出币种和金额。 * * @return 格式化后的字符串
     */
    @Override
    public String toString() {
        return String.format(" %s %s", currency.getCurrencyCode(), amount);
    }
}
