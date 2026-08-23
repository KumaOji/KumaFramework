package com.kuma.cloud.leetcode.p0124;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        Solution.TreeNode root = new Solution.TreeNode(
                1,
                new Solution.TreeNode(2),
                new Solution.TreeNode(3)
        );
        assertEquals(6, solution.maxPathSum(root));
    }

    @Test
    void example2() {
        Solution.TreeNode root = new Solution.TreeNode(
                -10,
                new Solution.TreeNode(9),
                new Solution.TreeNode(20, new Solution.TreeNode(15), new Solution.TreeNode(7))
        );
        assertEquals(42, solution.maxPathSum(root));
    }

    @Test
    void singleNode() {
        assertEquals(-3, solution.maxPathSum(new Solution.TreeNode(-3)));
    }
}
