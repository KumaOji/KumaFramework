package com.kuma.cloud.leetcode.p0206;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        Solution.ListNode head = node(1, node(2, node(3, node(4, node(5, null)))));
        Solution.ListNode reversed = solution.reverseList(head);
        assertEquals(List.of(5, 4, 3, 2, 1), toList(reversed));
    }

    @Test
    void emptyList() {
        assertNull(solution.reverseList(null));
    }

    @Test
    void singleNode() {
        Solution.ListNode head = new Solution.ListNode(1);
        Solution.ListNode reversed = solution.reverseList(head);
        assertEquals(List.of(1), toList(reversed));
    }

    private static Solution.ListNode node(int value, Solution.ListNode next) {
        return new Solution.ListNode(value, next);
    }

    private static List<Integer> toList(Solution.ListNode head) {
        java.util.ArrayList<Integer> values = new java.util.ArrayList<>();
        while (head != null) {
            values.add(head.val);
            head = head.next;
        }
        return values;
    }
}
