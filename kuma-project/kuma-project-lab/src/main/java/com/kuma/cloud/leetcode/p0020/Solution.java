package com.kuma.cloud.leetcode.p0020;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * 20. 有效的括号
 *
 * <p>栈：遇到左括号入栈，遇到右括号与栈顶匹配。
 * <p>时间复杂度：O(n)，空间复杂度：O(n)。
 */
public class Solution {

    private static final Map<Character, Character> PAIRS = Map.of(
            ')', '(',
            ']', '[',
            '}', '{'
    );

    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (PAIRS.containsValue(c)) {
                stack.push(c);
                continue;
            }
            if (stack.isEmpty() || stack.pop() != PAIRS.get(c)) {
                return false;
            }
        }
        return stack.isEmpty();
    }
}
