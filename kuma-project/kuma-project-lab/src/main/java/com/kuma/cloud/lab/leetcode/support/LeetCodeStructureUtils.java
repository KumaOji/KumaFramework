package com.kuma.cloud.lab.leetcode.support;

import com.kuma.cloud.leetcode.p0146.LruCacheSupport;

import java.util.List;
import java.util.Map;

/**
 * 委托 leetcode 模块同包 Support 类完成结构构建。
 */
public final class LeetCodeStructureUtils {

    private LeetCodeStructureUtils() {
    }

    public static List<Integer> toList(com.kuma.cloud.leetcode.p0023.Solution.ListNode head) {
        return com.kuma.cloud.leetcode.p0023.ListNodeSupport.toValues(head);
    }

    @SuppressWarnings("unchecked")
    public static com.kuma.cloud.leetcode.p0023.Solution.ListNode[] buildListNodeArray(List<?> lists) {
        return com.kuma.cloud.leetcode.p0023.ListNodeSupport.fromValueLists((List<List<Integer>>) lists);
    }

    public static List<Integer> toList206(com.kuma.cloud.leetcode.p0206.Solution.ListNode head) {
        return com.kuma.cloud.leetcode.p0206.ListNodeSupport.toValues(head);
    }

    public static com.kuma.cloud.leetcode.p0206.Solution.ListNode buildListNode206(List<Integer> values) {
        return com.kuma.cloud.leetcode.p0206.ListNodeSupport.fromValues(values);
    }

    public static com.kuma.cloud.leetcode.p0124.Solution.TreeNode buildTree124(List<Integer> values) {
        return com.kuma.cloud.leetcode.p0124.TreeNodeSupport.fromLevelOrder(values);
    }

    public static com.kuma.cloud.leetcode.p0236.Solution.TreeNode buildTree236(List<Integer> values) {
        return com.kuma.cloud.leetcode.p0236.TreeNodeSupport.fromLevelOrder(values);
    }

    public static com.kuma.cloud.leetcode.p0104.Solution.TreeNode buildTree104(List<Integer> values) {
        return com.kuma.cloud.leetcode.p0104.TreeNodeSupport.fromLevelOrder(values);
    }

    public static com.kuma.cloud.leetcode.p0236.Solution.TreeNode findNode236(
            com.kuma.cloud.leetcode.p0236.Solution.TreeNode root,
            int value
    ) {
        return com.kuma.cloud.leetcode.p0236.TreeNodeSupport.findNode(root, value);
    }

    public static Integer nodeValue236(com.kuma.cloud.leetcode.p0236.Solution.TreeNode node) {
        return com.kuma.cloud.leetcode.p0236.TreeNodeSupport.valueOf(node);
    }

    public static List<Integer> runLruCache(int capacity, List<Map<String, Object>> operations) {
        return LruCacheSupport.execute(capacity, operations);
    }

}
