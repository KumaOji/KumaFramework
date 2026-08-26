package com.kuma.cloud.lab.leetcode.config;

import com.kuma.cloud.lab.leetcode.runner.LeetCodeProblemRunner;
import com.kuma.cloud.lab.leetcode.runner.LeetCodeTestCase;
import com.kuma.cloud.lab.leetcode.support.LeetCodeInputUtils;
import com.kuma.cloud.lab.leetcode.support.LeetCodeStructureUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class LeetCodeStructureProblemRunnersConfiguration {

    @Bean
    List<LeetCodeProblemRunner> leetCodeStructureProblemRunners() {
        return List.of(
                runner(23, "合并 K 个升序链表", "HARD", List.of("linked-list", "heap"),
                        input -> LeetCodeStructureUtils.toList(new com.kuma.cloud.leetcode.p0023.Solution().mergeKLists(
                                LeetCodeStructureUtils.buildListNodeArray((List<?>) input.get("lists")))),
                        tc("example1", Map.of("lists", List.of(List.of(1, 4, 5), List.of(1, 3, 4), List.of(2, 6))),
                                List.of(1, 1, 2, 3, 4, 4, 5, 6))
                ),
                runner(104, "二叉树的最大深度", "EASY", List.of("tree", "dfs"),
                        input -> new com.kuma.cloud.leetcode.p0104.Solution().maxDepth(
                                LeetCodeStructureUtils.buildTree104(LeetCodeInputUtils.integerList(input, "root"))),
                        tc("example1", Map.of("root", levelOrder(3, 9, 20, null, null, 15, 7)), 3)
                ),
                runner(124, "二叉树中的最大路径和", "HARD", List.of("tree", "dfs"),
                        input -> new com.kuma.cloud.leetcode.p0124.Solution().maxPathSum(
                                LeetCodeStructureUtils.buildTree124(LeetCodeInputUtils.integerList(input, "root"))),
                        tc("example1", Map.of("root", List.of(1, -2, 3)), 3)
                ),
                runner(146, "LRU 缓存", "MEDIUM", List.of("design", "hash", "linked-list"),
                        input -> LeetCodeStructureUtils.runLruCache(
                                LeetCodeInputUtils.intValue(input, "capacity"),
                                LeetCodeInputUtils.operationList(input, "operations")),
                        tc("example1", Map.of(
                                        "capacity", 2,
                                        "operations", List.of(
                                                Map.of("op", "put", "key", 1, "value", 1),
                                                Map.of("op", "put", "key", 2, "value", 2),
                                                Map.of("op", "get", "key", 1),
                                                Map.of("op", "put", "key", 3, "value", 3),
                                                Map.of("op", "get", "key", 2),
                                                Map.of("op", "put", "key", 4, "value", 4),
                                                Map.of("op", "get", "key", 1),
                                                Map.of("op", "get", "key", 3),
                                                Map.of("op", "get", "key", 4)
                                        )
                                ),
                                List.of(1, -1, -1, 3, 4))
                ),
                runner(206, "反转链表", "EASY", List.of("linked-list"),
                        input -> LeetCodeStructureUtils.toList206(new com.kuma.cloud.leetcode.p0206.Solution().reverseList(
                                LeetCodeStructureUtils.buildListNode206(LeetCodeInputUtils.integerList(input, "head")))),
                        tc("example1", Map.of("head", List.of(1, 2, 3, 4, 5)), List.of(5, 4, 3, 2, 1))
                ),
                runner(236, "二叉树的最近公共祖先", "MEDIUM", List.of("tree", "dfs"),
                        input -> {
                            var root = LeetCodeStructureUtils.buildTree236(LeetCodeInputUtils.integerList(input, "root"));
                            int p = LeetCodeInputUtils.intValue(input, "p");
                            int q = LeetCodeInputUtils.intValue(input, "q");
                            var ancestor = new com.kuma.cloud.leetcode.p0236.Solution().lowestCommonAncestor(
                                    root,
                                    LeetCodeStructureUtils.findNode236(root, p),
                                    LeetCodeStructureUtils.findNode236(root, q)
                            );
                            return LeetCodeStructureUtils.nodeValue236(ancestor);
                        },
                        tc("example1", Map.of("root", levelOrder(3, 5, 1, 6, 2, 0, 8, null, null, 7, 4), "p", 5, "q", 1), 3)
                )
        );
    }

    private static LeetCodeProblemRunner runner(
            int number,
            String title,
            String difficulty,
            List<String> tags,
            Function<Map<String, Object>, Object> solver,
            LeetCodeTestCase... cases
    ) {
        return new LeetCodeProblemRunner.FunctionalLeetCodeProblemRunner(
                number, title, difficulty, tags, solver, List.of(cases));
    }

    private static LeetCodeTestCase tc(String name, Map<String, Object> input, Object expected) {
        return new LeetCodeTestCase(name, input, expected);
    }

    @SafeVarargs
    private static List<Integer> levelOrder(Integer... values) {
        return Arrays.asList(values);
    }

}
