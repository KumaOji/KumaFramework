package com.kuma.cloud.leetcode.p0200;

/**
 * 200. 岛屿数量
 *
 * <p>DFS：遇到陆地则沉岛并递归搜索四邻域。
 * <p>时间复杂度：O(m × n)，空间复杂度：O(m × n)（递归栈）。
 */
public class Solution {

    public int numIslands(char[][] grid) {
        if (grid.length == 0 || grid[0].length == 0) {
            return 0;
        }
        int count = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == '1') {
                    count++;
                    sinkIsland(grid, row, col);
                }
            }
        }
        return count;
    }

    private void sinkIsland(char[][] grid, int row, int col) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] != '1') {
            return;
        }
        grid[row][col] = '0';
        sinkIsland(grid, row - 1, col);
        sinkIsland(grid, row + 1, col);
        sinkIsland(grid, row, col - 1);
        sinkIsland(grid, row, col + 1);
    }
}
