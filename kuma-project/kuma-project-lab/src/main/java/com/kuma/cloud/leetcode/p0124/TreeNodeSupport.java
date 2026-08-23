package com.kuma.cloud.leetcode.p0124;

import java.util.List;

/**
 * 供 Lab 等外部模块调用的二叉树构建工具。
 */
public final class TreeNodeSupport {

    private TreeNodeSupport() {
    }

    public static Solution.TreeNode fromLevelOrder(List<Integer> values) {
        if (values == null || values.isEmpty() || values.get(0) == null) {
            return null;
        }
        Solution.TreeNode[] nodes = new Solution.TreeNode[values.size()];
        for (int i = 0; i < values.size(); i++) {
            Integer value = values.get(i);
            if (value != null) {
                nodes[i] = new Solution.TreeNode(value);
            }
        }
        link(nodes);
        return nodes[0];
    }

    private static void link(Solution.TreeNode[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) {
                continue;
            }
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < nodes.length) {
                nodes[i].left = nodes[left];
            }
            if (right < nodes.length) {
                nodes[i].right = nodes[right];
            }
        }
    }

}
