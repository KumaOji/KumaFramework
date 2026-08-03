package com.kuma.cloud.leetcode.p0023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        Solution.ListNode[] lists = {
                buildList(1, 4, 5),
                buildList(1, 3, 4),
                buildList(2, 6)
        };
        assertArrayEquals(new int[]{1, 1, 2, 3, 4, 4, 5, 6}, toArray(solution.mergeKLists(lists)));
    }

    @Test
    void emptyInput() {
        assertNull(solution.mergeKLists(new Solution.ListNode[]{}));
    }

    @Test
    void allNullLists() {
        assertNull(solution.mergeKLists(new Solution.ListNode[]{null, null}));
    }

    private static Solution.ListNode buildList(int... values) {
        Solution.ListNode dummy = new Solution.ListNode(0);
        Solution.ListNode tail = dummy;
        for (int value : values) {
            tail.next = new Solution.ListNode(value);
            tail = tail.next;
        }
        return dummy.next;
    }

    private static int[] toArray(Solution.ListNode head) {
        int size = 0;
        for (Solution.ListNode node = head; node != null; node = node.next) {
            size++;
        }
        int[] result = new int[size];
        int index = 0;
        for (Solution.ListNode node = head; node != null; node = node.next) {
            result[index++] = node.val;
        }
        return result;
    }
}
