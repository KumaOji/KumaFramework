# LeetCode 练习

每道题使用一个四位题号包，结构固定为：

```text
src/main/java/com/kuma/cloud/leetcode/p0001/Solution.java
src/test/java/com/kuma/cloud/leetcode/p0001/SolutionTest.java
```

## 新增一道题

以第 2 题为例：

1. 复制 `p0001` 的源码和测试目录。
2. 将两个目录改名为 `p0002`。
3. 修改两个文件开头的包名为 `com.kuma.cloud.leetcode.p0002`。
4. 在 `Solution` 中写题解，在 `SolutionTest` 中写示例和边界测试。

统一使用 `Solution` 类名，依靠题号包区分不同题目。这样代码可以方便地复制到 LeetCode 编辑器。

## 运行测试

运行模块内全部题目：

```shell
gradlew :kuma-project:kuma-project-leetcode:test
```

只运行一道题：

```shell
gradlew :kuma-project:kuma-project-leetcode:test --tests "com.kuma.cloud.leetcode.p0001.SolutionTest"
```

在 IntelliJ IDEA 中也可以直接运行任意 `SolutionTest`。
