package com.kuma.cloud.reference.domain.model;

import com.kuma.cloud.reference.exception.OrderConflictException;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class OrderTest {
    private static ProductSnapshot product() {
        return new ProductSnapshot("BOOK-001", "DDD Book", new Money(new BigDecimal("99.00")));
    }

    @Test
    void calculatesExactTotalAndAllowsOnlyOneConfirmation() {
        var order = Order.create(product(), 3, Instant.parse("2026-01-01T00:00:00Z"));
        assertEquals(new BigDecimal("297.00"), order.total().amount());
        var confirmed = order.confirm();
        assertEquals(Order.Status.CONFIRMED, confirmed.status());
        assertEquals(1, confirmed.version());
        assertEquals(Order.Status.CREATED, order.status());
        assertThrows(OrderConflictException.class, confirmed::confirm);
    }

    @Test
    void refusesInvalidQuantitiesNegativeAmountsAndSilentRounding() {
        assertThrows(IllegalArgumentException.class, () -> Order.create(product(), 0, Instant.now()));
        assertThrows(IllegalArgumentException.class, () -> Order.create(product(), 1001, Instant.now()));
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("-1.00")));
        assertThrows(ArithmeticException.class, () -> new Money(new BigDecimal("1.001")));
        assertThrows(IllegalArgumentException.class,
                () -> new Money(new BigDecimal("99999999999999999.99")).multiply(2));
    }
}
