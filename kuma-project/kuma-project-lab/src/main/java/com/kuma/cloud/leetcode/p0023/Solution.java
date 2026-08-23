package com.kuma.cloud.leetcode.p0023;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 23. 合并 K 个升序链表
 *
 * <p>最小堆：每次取出堆顶最小节点，将其 next 入堆。
 * <p>时间复杂度：O(N log k)，空间复杂度：O(k)。
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

    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> minHeap = new PriorityQueue<>(Comparator.comparingInt(node -> node.val));
        for (ListNode head : lists) {
            if (head != null) {
                minHeap.offer(head);
            }
        }
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (!minHeap.isEmpty()) {
            ListNode node = minHeap.poll();
            tail.next = node;
            tail = tail.next;
            if (node.next != null) {
                minHeap.offer(node.next);
            }
        }
        return dummy.next;
    }
}
