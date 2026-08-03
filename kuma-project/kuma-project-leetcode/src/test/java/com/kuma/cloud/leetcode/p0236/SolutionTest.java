package com.kuma.cloud.leetcode.p0236;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        Solution.TreeNode node5 = new Solution.TreeNode(5);
        Solution.TreeNode node1 = new Solution.TreeNode(1);
        Solution.TreeNode node4 = new Solution.TreeNode(4);
        Solution.TreeNode node6 = new Solution.TreeNode(6);
        Solution.TreeNode node2 = new Solution.TreeNode(2);
        Solution.TreeNode node0 = new Solution.TreeNode(0);
        Solution.TreeNode node8 = new Solution.TreeNode(8);
        Solution.TreeNode node7 = new Solution.TreeNode(7);
        Solution.TreeNode node3 = new Solution.TreeNode(3);

        Solution.TreeNode root = new Solution.TreeNode(3, node5, node1);
        node5.left = node6;
        node5.right = node2;
        node1.left = node0;
        node1.right = node8;
        node2.left = node7;
        node2.right = node4;

        assertSame(root, solution.lowestCommonAncestor(root, node5, node1));
    }

    @Test
    void example2() {
        Solution.TreeNode node5 = new Solution.TreeNode(5);
        Solution.TreeNode node1 = new Solution.TreeNode(1);
        Solution.TreeNode node4 = new Solution.TreeNode(4);
        Solution.TreeNode node6 = new Solution.TreeNode(6);
        Solution.TreeNode node2 = new Solution.TreeNode(2);
        Solution.TreeNode node0 = new Solution.TreeNode(0);
        Solution.TreeNode node8 = new Solution.TreeNode(8);
        Solution.TreeNode node7 = new Solution.TreeNode(7);
        Solution.TreeNode node3 = new Solution.TreeNode(3);

        Solution.TreeNode root = new Solution.TreeNode(3, node5, node1);
        node5.left = node6;
        node5.right = node2;
        node1.left = node0;
        node1.right = node8;
        node2.left = node7;
        node2.right = node4;

        assertSame(node5, solution.lowestCommonAncestor(root, node5, node4));
    }
}
