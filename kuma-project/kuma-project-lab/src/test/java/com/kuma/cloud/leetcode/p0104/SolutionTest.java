package com.kuma.cloud.leetcode.p0104;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        Solution.TreeNode root = new Solution.TreeNode(3,
                new Solution.TreeNode(9),
                new Solution.TreeNode(20, new Solution.TreeNode(15), new Solution.TreeNode(7)));
        assertEquals(3, solution.maxDepth(root));
    }

    @Test
    void emptyTree() {
        assertEquals(0, solution.maxDepth(null));
    }

    @Test
    void singleNode() {
        assertEquals(1, solution.maxDepth(new Solution.TreeNode(1)));
    }
}
