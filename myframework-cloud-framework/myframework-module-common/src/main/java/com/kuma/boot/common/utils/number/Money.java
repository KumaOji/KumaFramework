/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.kuma.boot.common.utils.number;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public class Money
implements Comparable<Money>,
Serializable {
    private long cent;
    private Currency currency;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    private Money(long cent, Currency currency) {
        this.cent = cent;
        this.currency = currency;
    }

    public static Money of(long minorUnits, String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        Preconditions.checkNotNull((Object)currency, (Object)("currency can not be null: " + currencyCode));
        return Money.of(minorUnits, currency);
    }

    public static Money of(long minorUnits, Currency currency) {
        Money money = new Money(0L, currency);
        money.cent = minorUnits;
        return money;
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        Preconditions.checkNotNull((Object)currency, (Object)("currency can not be null: " + currencyCode));
        return Money.of(amount, currency);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        Money money = new Money(0L, currency);
        money.cent = amount.movePointRight(currency.getDefaultFractionDigits()).longValue();
        return money;
    }

    public BigDecimal getAmount() {
        return BigDecimal.valueOf(this.cent, this.currency.getDefaultFractionDigits());
    }

    public long getCent() {
        return this.cent;
    }

    public final int getCentFactor() {
        return this.currency.getDefaultFractionDigits();
    }

    public Money divide(long value) {
        return this.divide(value, DEFAULT_ROUNDING);
    }

    public Money divide(BigDecimal value) {
        BigDecimal newCent = BigDecimal.valueOf(this.cent).divide(value, DEFAULT_ROUNDING);
        return Money.of(newCent.longValue(), this.currency);
    }

    public Money divide(BigDecimal value, RoundingMode roundingMode) {
        BigDecimal newCent = BigDecimal.valueOf(this.cent).divide(value, roundingMode);
        return Money.of(newCent.longValue(), this.currency);
    }

    public Money divide(long value, RoundingMode roundingMode) {
        BigDecimal newCent = BigDecimal.valueOf(this.cent).divide(BigDecimal.valueOf(value), roundingMode);
        return Money.of(newCent.longValue(), this.currency);
    }

    public Money add(Money value) {
        this.assertSameCurrency(value);
        return Money.of(this.cent + value.cent, this.currency);
    }

    public Money subtract(Money value) {
        this.assertSameCurrency(value);
        return Money.of(this.cent - value.cent, this.currency);
    }

    public Money multiply(long value) {
        return Money.of(this.cent * value, this.currency);
    }

    public Money multiply(BigDecimal value, RoundingMode roundingMode) {
        long newCent = BigDecimal.valueOf(this.cent).multiply(value).setScale(0, roundingMode).longValue();
        return Money.of(newCent, this.currency);
    }

    public Money multiply(BigDecimal value) {
        long newCent = BigDecimal.valueOf(this.cent).multiply(value).setScale(0, DEFAULT_ROUNDING).longValue();
        return Money.of(newCent, this.currency);
    }

    private void assertSameCurrency(Money value) {
        if (!Objects.equals(this.currency, value.currency)) {
            throw new IllegalArgumentException("Money instances must have the same currency");
        }
    }

    @Override
    public int compareTo(Money value) {
        this.assertSameCurrency(value);
        return Long.compare(this.cent, value.cent);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Money) {
            Money value = (Money)obj;
            return this.cent == value.cent && Objects.equals(this.currency, value.currency);
        }
        return false;
    }
}

