package com.kuma.cloud.leetcode.p0236;

/**
 * 236. 二叉树的最近公共祖先
 *
 * <p>后序 DFS：若当前节点是 p 或 q，或左右子树各找到一个目标，则当前节点为 LCA。
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

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) {
            return root;
        }
        return left != null ? left : right;
    }
}
