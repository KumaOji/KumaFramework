# Java 语法与对象基础实验

在 console → Lab 中搜索“Java 基础”“volatile”“对象”“Mark Word”或“类加载”，选择终端实验卡片查看命令、源码和预期结果。`violated` 在本组中按 Java 关键字 `volatile` 处理。

使用 JDK 25，从仓库根目录运行；无需启动 LabApplication、Nacos 或数据库。Gradle 使用模块已有的 JOL 依赖，首次运行需解析项目依赖。

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:javaBasicsLab
```

通过 `-PjavaBasicsLesson=syntax` 选择单项，可选 `all`、`syntax`、`volatile`、`objects`、`markword`、`classloading`、`inheritance`、`abstract`、`initialization`、`passing`。每次启动独立 JVM。所有检查均显式抛异常，不依赖 `-ea`；失败时进程以非零状态退出。

| 单项 | 观察内容 | 预期证据 |
| --- | --- | --- |
| syntax | 自增、除法、窄化溢出、移位、短路、循环、switch、字符串、装箱、泛型、集合、Stream、资源异常 | 自增表达式12，变量7；byte转换为-126；集合快照不随原集合追加；资源按second、first关闭，主异常携带两个suppressed异常 |
| volatile | payload发布、复合更新、原子类对照 | payload=42；两个线程各更新100次，固定交错的volatile计数100，AtomicInteger计数200 |
| objects | 引用副本、字段隐藏、动态分派、record相等、HashSet、集合拷贝 | 调用方对象值为2；parent-field/child-method；HashSet大小1；共享集合2项，复制集合1项 |
| markword | 同一对象fresh/hash/held/released、字段布局 | 四次真实布局、持锁检查通过、解锁后identity hash不变 |
| classloading | 加载与初始化、常量、父子构造、委派、类型身份 | initialize=false不触发static；父类先初始化；static只一次、实例部分执行两次；同名类由不同加载器定义后不可赋值 |
| inheritance | override/overload、字段与static隐藏、super、向上/向下转型 | 方法按运行时对象分派；字段/static按声明类型选择；重载按编译期签名选择；错误强转抛ClassCastException |
| abstract | 抽象类构造器与状态、接口默认方法、final模板、具体/匿名子类 | Rectangle面积12，匿名子类面积7；模板组合name与area |
| initialization | static、字段、实例块、父子构造链、this构造链、构造期间动态分派 | 父类构造时子类字段为0，构造完成为20；第二次构造不重复static；this链只初始化一次 |
| passing | 基本类型、对象、String、数组、List、swap | 基本类型1；对象/数组元素2；String不变；集合增加second；swap不能交换调用方变量 |

## volatile：如何解释结果

写线程先写普通字段 `payload`，再写 `volatile ready=true`；读线程观察到该volatile写入后读取payload。读线程有超时和中断检查，不用可能永久运行的普通字段空循环演示可见性。

非原子性实验把 `counter++` 的读取、加一、写回显式展开。每轮两个线程先读同一值，通过屏障后分别写回，再通过屏障进入下一轮，保证丢失一次更新。屏障自身提供同步；此部分用于展示复合操作的问题，不用于比较普通字段与volatile的可见性，也不声称自然调度下一定出现固定次数的丢失。

## 对象与相等

Java传递的是值；引用类型参数收到的是引用的副本。`record` 的成员引用不可重新赋值，但成员指向的集合仍可变。示例比较的是共享成员与独立集合快照。参与equals/hashCode的成员应在作为哈希集合键期间保持稳定。

## Mark Word：观测边界

实验第一次打印布局前不调用目标对象的toString/hashCode/identityHashCode，避免污染fresh状态。随后对同一对象计算identity hash、持锁观察、释放锁后观察。持锁时不再次计算hash，以减少观测操作对锁状态的影响。

JOL 0.17是现有项目依赖；在新JVM上可能提示无法动态attach或无法获取Serviceability Agent。输出会包含其估算条件。对象头、压缩指针、压缩对象头、对齐方式及锁实现受VM参数和版本影响，本实验不把固定字节数、某个二进制位模式或“锁一定升级”作为通过条件。JDK25实验不使用偏向锁。不要把System.identityHashCode解释为对象内存地址。

## 类加载：观测边界

同一个定义类加载器中，类初始化只执行一次；实验必须在新JVM中运行。`Class.forName(name, false, loader)`只加载，`true`触发初始化；编译期常量在调用处内联，不触发目标类初始化。构造对象时父类实例初始化和构造器先于子类实例初始化和构造器。

独立加载器仅对无依赖的实验payload读取class字节并defineClass，其余类型继续双亲委派。类型身份由类名与定义加载器共同决定。`String.class.getClassLoader()==null`表示bootstrap加载器，不表示没有加载过程。此示例观察加载和初始化行为，没有尝试断言验证、准备、解析的内部执行时间。

## 继承：先选签名，再选实现

实验同时持有 `Parent parent = new Child()` 和 `Child child`。实例方法重写按实际对象分派；同名字段和static方法按引用声明类型选择。重载先根据调用处声明类型选签名，再对该签名动态分派，因此 `parent.accept("hello")` 选择Object签名的子类实现，`child.accept("hello")` 选择String签名。

向上转型不创建新对象。向下转型只有对象实际属于目标类型时才成功；实验捕获预期ClassCastException，同时展示instanceof检查。`super.describe()`直接调用父类实现。

## 抽象类：可执行行为和编译约束

`Shape` 保存名称并提供构造器、抽象area方法和final模板report方法；`Rectangle`实现area，接口`Named`提供默认label方法。实验通过抽象类型引用调用具体实现，并创建匿名具体子类。抽象类可以具有字段、构造器和具体方法。

以下是独立的**预期编译失败反例**，不要粘贴进正常实验源码。分别保存为同名java文件后执行 `javac 文件名.java`；预期非零退出，诊断文字随JDK语言环境变化。这些反例在教程中供手动编译，正常实验只运行合法代码。

```java
// NewAbstract.java：抽象类不能直接实例化
abstract class NewAbstract { static Object bad() { return new NewAbstract(); } }
```

```java
// MissingImplementation.java：非抽象子类必须实现继承的抽象方法
abstract class RequiredMethod { abstract int value(); }
class MissingImplementation extends RequiredMethod {}
```

```java
// OverrideFinal.java：final方法不能重写
class FinalParent { final void run() {} }
class OverrideFinal extends FinalParent { @Override void run() {} }
```

## 对象初始化：构造期间的默认值

首次new的日志顺序为：父类static、子类static、父类字段、父类实例块、父类构造器、子类重写方法观察到字段0、子类字段、子类实例块、子类带参构造器、子类无参构造器体。第二次new只重复实例部分。

子类无参构造器通过this调用带参构造器；实例字段和实例块只执行一次。父类构造器调用可重写方法会动态分派到子类，但此时子类字段尚未执行显式初始化。实际业务应避免在构造期间调用可重写方法或发布未完成对象。

本例采用传统的super在前构造顺序。JDK25允许受限的构造器前置语句，本实验不覆盖该变体。static只初始化一次，因此每个实验入口均在独立JVM运行。

## 值传递：修改成员与重绑形参

基本类型参数复制数值，引用类型参数复制引用值。通过引用副本修改同一个对象/数组/集合会对调用方可见；给参数变量重新赋值只改变局部副本。String拼接得到新字符串，不能替换调用方变量。swap交换的也是两个局部引用副本。

需要替换调用方值时，可返回新值并由调用方接收。实验把六种场景逐一打印和检查，避免将“对象修改可见”误认为Java按引用传递。
