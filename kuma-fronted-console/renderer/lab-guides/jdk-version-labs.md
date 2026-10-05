# 各版本 JDK 功能实验（运行环境：JDK 25）

版本标签表示功能正式引入的版本，所有示例统一使用你的 JDK 25 编译、运行。
这是一组代表性功能实验，不是所有版本的完整变更清单，也不是在历史 JDK 上运行的兼容性矩阵。
比如 JDK 8 实验为了便于检查可以使用后续版本的 List.of；JDK 12 的 teeing 示例借助 JDK 16 的 record 表达结果。
实验直接运行，不依赖 Spring、Nacos 或第三方库；检查失败抛异常，不依赖 `-ea`。

## 运行

在仓库根目录执行（java、javac 均使用 JDK 25）：

```powershell
# 全部正式功能，包括 JDK25 紧凑源文件入口
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-jdk-version-labs.ps1
# 只运行一个功能版本
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-jdk-version-labs.ps1 -Version 21
# 额外启用独立 JDK25 预览实验
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-jdk-version-labs.ps1 -Version 25 -IncludePreview
```

IDE 入口：`com.kuma.cloud.lab.jdk.JdkVersionLearningDemo.main()`，可传 `all` 或版本号。
IDE 主入口执行包内正式功能；独立紧凑源文件和预览示例通过上述脚本运行。
所有预览源码放在 `examples/jdk25-preview/`，不影响正常 Gradle 编译，项目无需全局开启预览。
脚本在 `build/jdk-version-lab/` 下输出产物。

## 实验地图

| 功能正式版本 | 实验内容 | 核心检查 | 源码方法 |
| --- | --- | --- | --- |
| 8 | Lambda、Stream、Optional、java.time、CompletableFuture | 过滤/映射值、默认值、闰日、异步组合 42 | EarlyJdkLessons.jdk8 |
| 9 | List.of、takeWhile/dropWhile、模块归属 | 拒绝修改/null；前缀操作；String 属于 java.base | EarlyJdkLessons.jdk9 |
| 10 | var、List.copyOf | 编译期推断；快照不跟随原列表修改 | EarlyJdkLessons.jdk10 |
| 11 | String 新 API、Files 字符串读写、HTTP Client | blank/strip/repeat/lines；UTF-8 往返；HTTP 场景在18实验组合验证 | EarlyJdkLessons.jdk11 |
| 12 | Collectors.teeing | 同一次收集合并数量与总和 | EarlyJdkLessons.jdk12 |
| 14 | switch 表达式、yield | 箭头分支返回值 | LanguageLessons.jdk14 |
| 15 | Text blocks | 公共缩进移除、保留末尾换行 | LanguageLessons.jdk15 |
| 16 | record、instanceof 模式、Stream.toList | 值相等、构造验证、绑定变量、不可修改列表 | LanguageLessons.jdk16 |
| 17 | sealed classes | 允许子类型和反射元数据 | LanguageLessons.jdk17 |
| 18 | SimpleFileServer | 临时文件 HTTP 200、不存在文件404；客户端是11正式的 HTTP Client | RecentJdkLessons.jdk18 |
| 21 | 虚拟线程、Sequenced Collections、record patterns、模式 switch | 128个虚拟线程任务结果；反向视图；sealed 穷举解构 | RecentJdkLessons.jdk21 |
| 22 | FFM 外部内存、未命名模式 | 分配读写；越界、跨线程、关闭后访问拒绝；`_` 不绑定值 | RecentJdkLessons.jdk22 |
| 24 | Stream Gatherers、Class-File API | 分窗/前缀和；生成、解析、验证并执行字节码 | RecentJdkLessons.jdk24 |
| 25 | ScopedValue、module import、灵活构造器 | 嵌套恢复、异常清理、线程隔离；super 前验证不触发父构造器 | Jdk25Lesson.run |
| 25 | 紧凑源文件、实例 main、IO.println | 无显式 class 的程序汇总为6 | examples/jdk25/CompactMain.java |
| 25 预览 | 原始类型模式、primitive switch | 127 精确匹配 byte，128 不匹配；long 匹配 int | examples/jdk25-preview/PrimitivePatterns.java |

## 阅读时关注的边界

- 集合“不可修改”不等于元素不可变。copyOf 是容器快照，不是深拷贝；reversed 是底层列表的反向视图。
- var 是编译期类型推断，不是动态类型。record 默认是浅层不可变，组件引用指向的对象仍可能可变。
- CompletableFuture 示例验证结果组合，不比较线程池性能；虚拟线程适合高并发阻塞任务，128个任务的成功不能证明CPU计算加速。
- HTTP 文件实验仅绑定回环临时端口，退出时关闭服务并删除自己创建的临时文件。
- FFM 使用 confined Arena；示例验证线程所有权和生命周期，没有链接本机C库，不需要 native-access 选项。
- Class-File API 在内存中生成一个返回42的方法，经解析、验证后由独立类加载器加载；不写入或修改项目类文件。
- ScopedValue 每个任务明确建立自己的绑定，不假定普通线程或线程池自动继承。StructuredTaskScope 的继承场景属于另一项预览API，本批不混入正式功能。
- 构造器前置代码仍须遵守对象早期构造限制，不能随意调用未初始化对象的实例方法。

## 为什么不是每个版本都有独立实验

部分版本主要推进 JVM 实现或预览轮次；本批按代表性功能的**正式版本**收录，避免将仍在演变的历史预览语法重复放入JDK25工程。

| 历史阶段 | 本批处理 |
| --- | --- |
| 12/13 的 switch 预览 | 使用14正式版本；13引入的 yield 语法在14实验演示 |
| 13/14 的文本块预览 | 使用15正式版本 |
| 14/15 的 record 预览 | 使用16正式版本 |
| 15/16 的 sealed 预览 | 使用17正式版本 |
| 17～20 的模式 switch、19/20 的 record patterns、19/20 的虚拟线程预览 | 使用21正式版本 |
| 22/23 的 Gatherers、Class-File API 预览 | 使用24正式版本 |
| 21～24 的紧凑源文件、22～24 的灵活构造器、23/24 的 module import 预览 | 使用25正式版本 |
| 21/22 的 String Templates | 后续撤回，未加入 JDK25 实验 |

JDK25 原始类型模式仍为预览，脚本同时为编译和运行添加 `--enable-preview`；预览class文件需匹配对应JDK版本。

## 官方资料

- [Oracle 按版本整理的语言变化](https://docs.oracle.com/en/java/javase/25/language/java-language-changes-release.html)
- [JDK21 虚拟线程 JEP444](https://openjdk.org/jeps/444)
- [Sequenced Collections](https://docs.oracle.com/en/java/javase/21/core/creating-sequenced-collections-sets-and-maps.html)
- [FFM Arena](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/Arena.html)
- [Gatherers](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/stream/Gatherers.html)
- [Class-File API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/classfile/ClassFile.html)
- [ScopedValue](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/ScopedValue.html)
