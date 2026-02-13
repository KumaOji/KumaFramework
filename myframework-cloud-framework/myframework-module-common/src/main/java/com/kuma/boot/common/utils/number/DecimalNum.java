/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.number;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalNum
extends Number {
    private BigDecimal value;

    public BigDecimal getValue() {
        return this.value;
    }

    public DecimalNum() {
        this(new BigDecimal(0));
    }

    public DecimalNum(BigDecimal decimal) {
        this.value = decimal;
    }

    public static DecimalNum of(BigDecimal decimal) {
        return new DecimalNum(decimal);
    }

    public static DecimalNum of(String decimal) {
        return DecimalNum.of(new BigDecimal(decimal));
    }

    public static DecimalNum of(double decimal) {
        return DecimalNum.of(BigDecimal.valueOf(decimal));
    }

    public static DecimalNum of(long decimal) {
        return DecimalNum.of(BigDecimal.valueOf(decimal));
    }

    public DecimalNum add(String decimal) {
        return this.add(new BigDecimal(decimal));
    }

    public DecimalNum add(long decimal) {
        return this.add(BigDecimal.valueOf(decimal));
    }

    public DecimalNum add(double decimal) {
        return this.add(BigDecimal.valueOf(decimal));
    }

    public DecimalNum add(BigDecimal decimal) {
        this.value = this.value.add(decimal);
        return this;
    }

    public DecimalNum subtract(String decimal) {
        return this.subtract(new BigDecimal(decimal));
    }

    public DecimalNum subtract(long decimal) {
        return this.subtract(BigDecimal.valueOf(decimal));
    }

    public DecimalNum subtract(double decimal) {
        return this.subtract(BigDecimal.valueOf(decimal));
    }

    public DecimalNum subtract(BigDecimal decimal) {
        this.value = this.value.subtract(decimal);
        return this;
    }

    public DecimalNum multiply(String decimal) {
        return this.multiply(new BigDecimal(decimal));
    }

    public DecimalNum multiply(long decimal) {
        return this.multiply(BigDecimal.valueOf(decimal));
    }

    public DecimalNum multiply(double decimal) {
        return this.multiply(BigDecimal.valueOf(decimal));
    }

    public DecimalNum multiply(BigDecimal decimal) {
        this.value = this.value.multiply(decimal);
        return this;
    }

    public DecimalNum divide(String decimal) {
        return this.divide(new BigDecimal(decimal));
    }

    public DecimalNum divide(String decimal, RoundingMode roundingMode) {
        return this.divide(new BigDecimal(decimal), roundingMode);
    }

    public DecimalNum divide(long decimal) {
        return this.divide(BigDecimal.valueOf(decimal));
    }

    public DecimalNum divide(long decimal, RoundingMode roundingMode) {
        return this.divide(BigDecimal.valueOf(decimal), roundingMode);
    }

    public DecimalNum divide(double decimal) {
        return this.divide(BigDecimal.valueOf(decimal));
    }

    public DecimalNum divide(double decimal, RoundingMode roundingMode) {
        return this.divide(BigDecimal.valueOf(decimal), roundingMode);
    }

    public DecimalNum divide(BigDecimal decimal) {
        this.value = this.value.divide(decimal);
        return this;
    }

    public DecimalNum divide(BigDecimal decimal, RoundingMode roundingMode) {
        this.value = this.value.divide(decimal, roundingMode);
        return this;
    }

    public DecimalNum scale(int scale) {
        return this.scale(scale, RoundingMode.HALF_EVEN);
    }

    public DecimalNum scale(int scale, RoundingMode roundingMode) {
        this.value = this.value.setScale(scale, roundingMode);
        return this;
    }

    @Override
    public int intValue() {
        return this.value.intValue();
    }

    @Override
    public long longValue() {
        return this.value.longValue();
    }

    @Override
    public float floatValue() {
        return this.value.floatValue();
    }

    @Override
    public double doubleValue() {
        return this.value.doubleValue();
    }

    public String toString() {
        return this.value.toString();
    }
}

