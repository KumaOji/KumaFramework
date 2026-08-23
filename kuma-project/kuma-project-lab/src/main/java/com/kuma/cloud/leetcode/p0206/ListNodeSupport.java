package com.kuma.cloud.leetcode.p0206;

import java.util.ArrayList;
import java.util.List;

/**
 * 供 Lab 等外部模块调用的链表构建工具。
 */
public final class ListNodeSupport {

    private ListNodeSupport() {
    }

    public static Solution.ListNode fromValues(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        Solution.ListNode head = new Solution.ListNode(values.get(0));
        Solution.ListNode current = head;
        for (int i = 1; i < values.size(); i++) {
            current.next = new Solution.ListNode(values.get(i));
            current = current.next;
        }
        return head;
    }

    public static List<Integer> toValues(Solution.ListNode head) {
        List<Integer> values = new ArrayList<>();
        while (head != null) {
            values.add(head.val);
            head = head.next;
        }
        return values;
    }

}
