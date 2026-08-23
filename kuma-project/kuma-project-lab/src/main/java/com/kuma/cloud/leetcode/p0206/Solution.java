package com.kuma.cloud.leetcode.p0206;

/**
 * 206. 反转链表
 *
 * <p>迭代：逐个将当前节点的 next 指向 prev。
 * <p>时间复杂度：O(n)，空间复杂度：O(1)。
 */
public class Solution {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode current = head;
        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        return prev;
    }
}
