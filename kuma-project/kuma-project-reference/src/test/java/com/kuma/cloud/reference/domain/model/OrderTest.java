package com.kuma.cloud.reference.domain.model;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.utils.number.Money;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class OrderTest {
    private static ProductSnapshot product() {
        return new ProductSnapshot("BOOK-001", "DDD Book", Money.of(new BigDecimal("99.00"), "CNY"));
    }

    @Test
    void calculatesExactTotalAndAllowsOnlyOneConfirmation() {
        var order = Order.create(product(), 3, Instant.parse("2026-01-01T00:00:00Z"));
        assertEquals(new BigDecimal("297.00"), order.total().getAmount());
        var confirmed = order.confirm();
        assertEquals(Order.Status.CONFIRMED, confirmed.status());
        assertEquals(1, confirmed.version());
        assertEquals(Order.Status.CREATED, order.status());
        assertThrows(BusinessException.class, confirmed::confirm);
    }

    @Test
    void refusesInvalidQuantitiesNegativeAmountsAndSilentRounding() {
        assertThrows(IllegalArgumentException.class, () -> Order.create(product(), 0, Instant.now()));
        assertThrows(IllegalArgumentException.class, () -> Order.create(product(), 1001, Instant.now()));
        assertThrows(IllegalArgumentException.class, () -> new ProductSnapshot("BOOK-001", "Book", Money.of(new BigDecimal("-1.00"), "CNY")));
        assertThrows(ArithmeticException.class, () -> Money.of(new BigDecimal("1.001"), "CNY"));
        assertThrows(ArithmeticException.class,
                () -> Money.of(new BigDecimal("92233720368547758.07"), "CNY").multiply(2));
    }
}
