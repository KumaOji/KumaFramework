package com.kuma.cloud.reference.config;

import com.kuma.boot.common.utils.number.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FrameworkMoneyTest {
    @Test
    void decimalConversionRejectsHiddenRoundingAndMinorUnitOverflow() {
        assertEquals(new BigDecimal("99.00"), Money.of(new BigDecimal("99.000"), "CNY").getAmount());
        assertThrows(ArithmeticException.class, () -> Money.of(new BigDecimal("1.001"), "CNY"));
        assertThrows(ArithmeticException.class, () -> Money.of(new BigDecimal("92233720368547758.08"), "CNY"));
        assertThrows(ArithmeticException.class, () -> Money.of(new BigDecimal("1.1"), "JPY"));
    }

    @Test
    void arithmeticRejectsOverflowInsteadOfWrappingOrTruncating() {
        var largest = Money.of(Long.MAX_VALUE, "CNY");
        assertThrows(ArithmeticException.class, () -> largest.add(Money.of(1, "CNY")));
        assertThrows(ArithmeticException.class, () -> Money.of(Long.MIN_VALUE, "CNY").subtract(Money.of(1, "CNY")));
        assertThrows(ArithmeticException.class, () -> largest.multiply(2));
        assertThrows(ArithmeticException.class, () -> largest.multiply(new BigDecimal("2")));
        assertThrows(ArithmeticException.class, () -> largest.divide(new BigDecimal("0.1")));
    }

    @Test
    void operationsKeepCurrencyAndLeaveTheOriginalValueUnchanged() {
        var original = Money.of(new BigDecimal("1.01"), "CNY");
        assertEquals(new BigDecimal("0.51"), original.divide(2, RoundingMode.HALF_UP).getAmount());
        assertEquals(new BigDecimal("1.01"), original.getAmount());
        assertEquals(original, Money.of(101, "CNY"));
        assertEquals(original.hashCode(), Money.of(101, "CNY").hashCode());
        assertThrows(IllegalArgumentException.class, () -> original.add(Money.of(101, "USD")));
    }
}
