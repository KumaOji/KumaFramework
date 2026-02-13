/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.number;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public final class MoneyOther
implements Comparable<MoneyOther>,
Serializable {
    private static final long serialVersionUID = 1L;
    private final BigDecimal amount;
    private final Currency currency;

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

    public static MoneyOther of(Currency currency, BigDecimal amount) {
        return MoneyOther.of(currency, amount, RoundingMode.HALF_UP);
    }

    public static MoneyOther of(Currency currency, BigDecimal amount, RoundingMode roundingMode) {
        Objects.requireNonNull(currency, "Currency cannot be null.");
        Objects.requireNonNull(amount, "Amount cannot be null.");
        Objects.requireNonNull(roundingMode, "RoundingMode cannot be null.");
        BigDecimal scaledAmount = amount.setScale(currency.getDefaultFractionDigits(), roundingMode);
        return new MoneyOther(scaledAmount, currency);
    }

    public MoneyOther add(MoneyOther other) {
        this.validateSameCurrency(other);
        BigDecimal resultAmount = this.amount.add(other.amount);
        return new MoneyOther(resultAmount, this.currency);
    }

    public MoneyOther subtract(MoneyOther other) {
        this.validateSameCurrency(other);
        BigDecimal resultAmount = this.amount.subtract(other.amount);
        return new MoneyOther(resultAmount, this.currency);
    }

    public MoneyOther multiply(BigDecimal multiplier) {
        return this.multiply(multiplier, RoundingMode.HALF_UP);
    }

    public MoneyOther multiply(BigDecimal multiplier, RoundingMode roundingMode) {
        Objects.requireNonNull(multiplier, "Multiplier cannot be null.");
        Objects.requireNonNull(roundingMode, "RoundingMode cannot be null.");
        BigDecimal resultAmount = this.amount.multiply(multiplier).setScale(this.currency.getDefaultFractionDigits(), roundingMode);
        return new MoneyOther(resultAmount, this.currency);
    }

    public MoneyOther divide(BigDecimal divisor, int scale, RoundingMode roundingMode) {
        Objects.requireNonNull(divisor, "Divisor cannot be null.");
        Objects.requireNonNull(roundingMode, "RoundingMode cannot be null.");
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Division by zero.");
        }
        BigDecimal resultAmount = this.amount.divide(divisor, scale, roundingMode).setScale(this.currency.getDefaultFractionDigits(), roundingMode);
        return new MoneyOther(resultAmount, this.currency);
    }

    @Override
    public int compareTo(MoneyOther other) {
        this.validateSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public BigDecimal getAmountMinorUnit() {
        int fractionDigits = this.currency.getDefaultFractionDigits();
        return this.amount.movePointRight(fractionDigits);
    }

    public Currency getCurrency() {
        return this.currency;
    }

    private void validateSameCurrency(MoneyOther other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currencies do not match ");
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MoneyOther money = (MoneyOther)o;
        return this.amount.equals(money.amount) && this.currency.equals(money.currency);
    }

    public int hashCode() {
        return Objects.hash(this.amount, this.currency);
    }

    public String toString() {
        return String.format(" %s %s", this.currency.getCurrencyCode(), this.amount);
    }
}

