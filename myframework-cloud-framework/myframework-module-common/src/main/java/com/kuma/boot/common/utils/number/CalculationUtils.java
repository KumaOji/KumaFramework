/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.number;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public final class CalculationUtils {
    private CalculationUtils() {
    }

    private static BigDecimal calculate(Stream<BigDecimal> stream, BinaryOperator<BigDecimal> reduceFn) {
        return stream.filter(Objects::nonNull).reduce(reduceFn).orElse(BigDecimal.ZERO);
    }

    private static Stream<BigDecimal> variableArrayToStream(BigDecimal ... nx) {
        return Optional.ofNullable(nx).map(Arrays::stream).orElse(Stream.empty());
    }

    private static Stream<BigDecimal> getStream(BigDecimal n1, BigDecimal n2, BigDecimal ... nx) {
        return Stream.concat(Stream.of(n1, n2), CalculationUtils.variableArrayToStream(nx));
    }

    private static Stream<BigDecimal> listToStream(List<BigDecimal> nx) {
        return Optional.ofNullable(nx).map(Collection::stream).orElse(Stream.empty());
    }

    public static BigDecimal add(BigDecimal n1, BigDecimal n2, BigDecimal ... nx) {
        return CalculationUtils.calculate(CalculationUtils.getStream(n1, n2, nx), BigDecimal::add);
    }

    public static BigDecimal add(BigDecimal ... ns) {
        return CalculationUtils.calculate(CalculationUtils.variableArrayToStream(ns), BigDecimal::add);
    }

    public static BigDecimal add(List<BigDecimal> ns) {
        return CalculationUtils.calculate(CalculationUtils.listToStream(ns), BigDecimal::add);
    }

    public static BigDecimal subtract(BigDecimal n1, BigDecimal n2, BigDecimal ... nx) {
        return CalculationUtils.calculate(CalculationUtils.getStream(n1, n2, nx), BigDecimal::subtract);
    }

    public static BigDecimal subtract(BigDecimal ... ns) {
        return CalculationUtils.calculate(CalculationUtils.variableArrayToStream(ns), BigDecimal::subtract);
    }

    public static BigDecimal subtract(List<BigDecimal> ns) {
        return CalculationUtils.calculate(CalculationUtils.listToStream(ns), BigDecimal::subtract);
    }

    public static BigDecimal multiple(BigDecimal n1, BigDecimal n2, BigDecimal ... nx) {
        return CalculationUtils.calculate(CalculationUtils.getStream(n1, n2, nx), BigDecimal::multiply);
    }

    public static BigDecimal multiple(BigDecimal ... ns) {
        return CalculationUtils.calculate(CalculationUtils.variableArrayToStream(ns), BigDecimal::multiply);
    }

    public static BigDecimal multiple(List<BigDecimal> ns) {
        return CalculationUtils.calculate(CalculationUtils.listToStream(ns), BigDecimal::multiply);
    }

    private static BinaryOperator<BigDecimal> safeDivideFn(int scale, RoundingMode roundingMode) {
        return (x, y) -> y.compareTo(BigDecimal.ZERO) != 0 ? x.divide((BigDecimal)y, scale, roundingMode) : BigDecimal.ZERO;
    }

    public static BigDecimal divide(int scale, RoundingMode roundingMode, BigDecimal n1, BigDecimal n2, BigDecimal ... nx) {
        return CalculationUtils.calculate(CalculationUtils.getStream(n1, n2, nx), CalculationUtils.safeDivideFn(scale, roundingMode));
    }

    public static BigDecimal divide(int scale, RoundingMode roundingMode, BigDecimal ... ns) {
        return CalculationUtils.calculate(CalculationUtils.variableArrayToStream(ns), CalculationUtils.safeDivideFn(scale, roundingMode));
    }

    public static BigDecimal divide(int scale, RoundingMode roundingMode, List<BigDecimal> ns) {
        return CalculationUtils.calculate(CalculationUtils.listToStream(ns), CalculationUtils.safeDivideFn(scale, roundingMode));
    }

    public static BigDecimal divide(BigDecimal n1, BigDecimal n2, BigDecimal ... nx) {
        return CalculationUtils.divide(2, RoundingMode.HALF_UP, n1, n2, nx);
    }

    public static BigDecimal divide(BigDecimal ... ns) {
        return CalculationUtils.divide(2, RoundingMode.HALF_UP, ns);
    }

    public static BigDecimal divide(List<BigDecimal> ns) {
        return CalculationUtils.divide(2, RoundingMode.HALF_UP, ns);
    }
}

