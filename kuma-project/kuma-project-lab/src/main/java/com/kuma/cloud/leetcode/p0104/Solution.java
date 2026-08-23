package com.kuma.cloud.leetcode.p0104;

/**
 * 104. 二叉树的最大深度
 *
 * <p>递归：树的最大深度 = 1 + max(左子树深度, 右子树深度)。
 * <p>时间复杂度：O(n)，空间复杂度：O(h)。
 */
public class Solution {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }
}
