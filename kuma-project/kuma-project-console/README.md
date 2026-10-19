# Kuma 本机控制台

独立运行的本机监控项目，启动后自动打开 `http://127.0.0.1:18090`。HTML、CSS、JavaScript 全部内嵌在 JAR 中，无需 Node、前端构建、Nacos、数据库或 CDN。

前端源码统一放在独立模块 [kuma-fronted-console](../../kuma-fronted-console/README.md) 的 `renderer/`，Gradle 打包时复制到 JAR。双击该前端模块的 `start-console.cmd` 可打开 Electron 桌面程序；原有浏览器启动方式继续可用。

## 打开程序

Windows 双击 `start-console.cmd`，会先构建再在后台启动，自动用 Edge / Chrome 打开独立的应用窗口；没有这些浏览器时使用默认浏览器。重复打开会复用已运行的控制台。双击 `stop-console.cmd` 可停止后台服务。关闭浏览器窗口不会停止后台采样。需要本机 JDK 25。

也可以在 IDEA 中运行 `com.kuma.cloud.console.ConsoleApplication`，或从仓库根目录运行：

```powershell
.\gradlew.bat :kuma-project:kuma-project-console:bootRun
```

打包后直接运行：

```powershell
java --enable-preview --enable-native-access=ALL-UNNAMED -jar kuma-project/kuma-project-console/build/libs/kuma-console.jar
```

## 四个页面

- **本机概览**：CPU 型号、核心数与使用率、物理内存与交换空间、系统运行时间、挂载卷、网卡流量、主板/固件/显卡/内存条/电池、可用传感器、Windows TCP 连接与端口 PID 映射、全部可读取进程与线程数。进程可搜索并按 CPU、内存、PID 排序。驱动未提供的传感器标记为未提供。
- **WSL / k3s**：WSL 发行版状态、Linux 内存/存储/进程/监听端口；全部命名空间的节点、Pods、Deployment、StatefulSet、DaemonSet、ReplicaSet、Job、CronJob、Ingress、Service、PV、PVC 和事件。支持筛选、资源详情、容器日志和 metrics-server 用量。资源浏览器通过 API discovery 查询其他资源，包括 Strimzi Kafka 等 CRD。
- **项目服务**：默认探测 Blog、Lab、UAA、Gateway。区分健康、健康异常、HTTP 可达和无法连接，提供服务、Swagger 与健康检查入口，并自动识别运行中的 Kuma Java 项目。
- **Lab 实验室**：内置 Kafka、Redis、向量库、事务、SQL、Binlog、Spring、JNI 等实验；可从 Lab 的 OpenAPI 同步全部实验接口，编辑方法、路径、JSON 正文，发送请求并查看状态码、耗时和响应。页面不会自动执行实验。最近 20 次请求仅保存方法、路径、状态和时间，不保存正文与认证头。

本机每 2 秒采样、项目每 10 秒、WSL/k3s 每 15 秒。采样耗时会增加实际间隔。曲线保留当前页面会话最近 60 个采样点。首次 CPU 采样显示 `—`，而非假定为 0%。网络按网卡单独展示，避免虚拟网卡重复计入合计。采样错误会明确显示，不把离线服务显示成健康。

## 配置

WSL / k3s 页可选择已安装的发行版进行「启动发行版」「关闭发行版」，也可「关闭全部 WSL」。启动执行 `wsl --distribution … --exec /bin/sh -c "exec sleep infinity"` 并保持后台会话，关闭指定发行版执行 `wsl --terminate …`，关闭全部执行 `wsl --shutdown`；不删除发行版及其文件。关闭会停止其中的 Linux 服务与集群，启动后由发行版自身的 systemd 配置恢复服务。监控会先检查发行版是否运行，避免查询自动拉起已关闭的 WSL。Gateway 日志直接读取 Windows 本机文件。

Lab、Blog 后端、UAA、Gateway 启动时会检查构建缓存：已有 JAR 与源码、资源及 Gradle 配置一致时直接启动，跳过 Gradle；缺少产物或相关输入变化时才执行增量构建。共享 starter 的源码变化也会使缓存失效，其他应用源码变化不会触发该应用构建。缓存标记保存在 `build/startup-cache/`；Lab 按 JAR 内容复用运行副本，Blog/UAA/Gateway 继续使用已有 SHA256 运行副本。前端直接运行 Astro 开发服务，保留其开发缓存；知识仓库同步在内容、代码、配置及依赖锁文件未变化时复用现有页面。可设置 `KNOWLEDGE_FORCE_BUILD=1` 强制重新生成知识仓库。

「本地公共组件」位于 Lab 上方，支持 UAA 与 Gateway 的启动、停止、状态和日志。两者均在 Windows 本机运行，复用 `start-services-local.ps1 -Services uaa` 或 `-Services gateway`，自动读取 WSL 中间件地址，关闭调试端口。UAA 业务端口为 33336，Gateway 业务端口为 18080、管理端口为 18081。WSL 仅承载中间件与监控，不运行 UAA / Gateway 应用。`KUMA_COMPONENTS_DISTRIBUTION` 可指定中间件所在的 WSL 发行版，默认 `Ubuntu-24.04`。已运行的服务可被识别和停止，不会重复启动。

Blog 页面将控制入口收在默认折叠的「配置与服务」中，展开状态会记住。启动 Blog 时，如果本机后端可启动，会同时调用现有 `scripts/start-blog-local.ps1 -Build -DebugPort 0`，自动读取 WSL 中间件地址；也可单独启动、停止后端及查看后端日志。「全部停止」停止前端、知识仓库构建和 Blog 后端。后端默认使用 `Ubuntu-24.04`，可通过 `console.blog.backend-distribution` 指定发行版。折叠区域下方直接显示 Blog 页面，可切换博客和知识仓库，也可在新窗口打开。前端登录仍使用项目现有的 Gateway / UAA 配置。

启动 Blog 会先执行前端项目的 `scripts/sync-knowledge.mjs`，把同级 `KumaKnowledge` 构建到 Blog 的 `public/knowledge/`，成功后才启动前端。「同步知识仓库」按钮可独立更新内容，Blog 运行时也可使用；同步状态及构建日志会显示在 Blog 页面。停止 Blog 会取消进行中的知识仓库构建，并停止前端进程，因此 Blog 与 `/knowledge` 页面一起关闭。已生成的知识仓库文件保留。同步脚本支持通过 `KNOWLEDGE_ROOT` 指定知识仓库路径。

左侧导航的 **Blog** 页面控制仓库上级 `kuma-blog-fronted/KumaBlog` 的 Astro 前端，默认地址为 `http://localhost:4321`。支持启动、停止、打开网页、查看启动日志；也可识别并停止已从该项目启动的 Astro 开发进程。启动使用本机 Node 和项目现有的 Astro 依赖，缺少依赖时先在前端项目执行 `pnpm install`。`KUMA_BLOG_FRONTEND_DIR` 可指定包含 `package.json` 的 `KumaBlog` 目录，`KUMA_BLOG_FRONTEND_PORT` 可覆盖端口。由控制台启动的前端在控制台退出时一并停止；仅被识别的已有进程不会随控制台退出而停止。

补充的监控功能：

- 本机硬件面板支持 GPU 驱动用量、DNS、Windows 全部可读取系统服务及服务搜索。硬件、GPU 和系统服务每 30 秒采样。
- WSL 系统详情可切换任意运行中的发行版，查看系统版本、CPU 拓扑与 vmstat、完整内存信息、磁盘/文件系统、全部网卡、全部进程、监听端口与 systemd 服务。按需读取，结果缓存 15 秒；k3s 使用配置中指定的发行版。
- 项目页面自动识别运行中的 Kuma Application 主类或仓库 JAR，显示 PID、启动时间、累计 CPU 时间和实际监听端口。运行诊断可读取 Actuator 健康、信息、指标、JVM 内存/线程数、CPU、运行时间和线程转储。未开放/需认证的端点显示实际 HTTP 状态。
- Lab 已离线内置仓库全部 70 个实验接口，包含 Kafka 完整异步实验、实时步骤查询、topic / broker 检查与实验清理。`scripts/refresh-lab-catalog.py` 从 Controller / DTO 源码更新目录，正常使用控制台无需 Python。运行中的 Lab 仍可同步最新 OpenAPI；示例 JSON 需按业务填写。
- Lab 同时提供24个本机/Linux学习实验：并发与内存、Webhook/Socket、JDK各版本正式及预览功能。界面显示命令、代码入口、预期结果与离线说明，可按运行方式筛选；使用 `scripts/refresh-lab-learning.py` 同步学习目录。
- Lab 响应默认把 JSON 字符串内的换行和制表符显示为实际排版；可切换回 JSON 格式，复制按钮始终复制原始响应，保留 JSON 有效性和原始内容。
- Lab 增加六个可观测性中间件实验（Loki、Prometheus、Alertmanager、OTel、SkyWalking、Grafana），学习卡片包含可点击 UI 入口和具体页面位置。调用链查回实验允许120秒，其余请求仍为30秒。与 Prometheus 同时运行时将 Lab 改为19090端口，并设置 `KUMA_LAB_URL=http://127.0.0.1:19090/api`。
- 概览仅显示通用监控摘要，不固定展示业务项目名。WSL 默认自动选择一个已运行发行版；非 Windows 系统隐藏 WSL 导航，其他本机指标继续由 OSHI 采集。没有配置 Lab 服务时，监控后端仍能启动。
- 项目页面列出仓库全部 17 个有 Gradle 构建文件的应用模块（7 个启用、10 个未启用 Demo），支持按项目或 starter 搜索。每个项目可查看去重后的直接/间接 starter、逐层引入路径及完整模块依赖树，并关联发现的项目进程和已配置的服务状态。
- 启用项目的依赖目录由 `gradle/console-catalog.gradle` 在构建控制台时读取 Gradle 加载后的 `api` / `implementation` / `runtimeOnly` / `compileOnly` 声明，递归遍历仓库模块，处理依赖排除与 `transitive=false`，不包含测试依赖。第三方 starter 显示坐标，不解析其发布包内部依赖；这是一份构建声明目录，不代表运行时自动配置已激活。修改项目依赖后，停止并重新启动控制台即可重新构建目录。
- 未启用 Demo 没有可求值的 Gradle 模型，只识别源码中明确写出的 `project('…')` 依赖，动态表达式和第三方声明可能不完整；页面明确标记来源及缺失模块。

控制台有自己的 `application.yml`，与业务项目的 Nacos 配置独立。可通过环境变量覆盖：

| 环境变量 | 默认值 |
| --- | --- |
| `KUMA_CONSOLE_PORT` | `18090` |
| `KUMA_CONSOLE_OPEN_BROWSER` | `true` |
| `KUMA_CONSOLE_WSL_DISTRIBUTION` | `auto`（首个运行中的发行版） |
| `KUMA_CONSOLE_WSL_USER` | `root` |
| `KUMA_BLOG_URL` | `http://127.0.0.1:9000/api` |
| `KUMA_LAB_URL` | `http://127.0.0.1:19090/api` |
| `KUMA_UAA_URL` | `http://127.0.0.1:33336` |
| `KUMA_GATEWAY_URL` | `http://127.0.0.1:18080` |

`console.projects` 可添加其他正在运行的项目；Lab 工作台使用名称为 `Lab` 的地址。地址包含应用的 context-path，例如 `/api`。接口路径填写 `/lab/kafka/status`，无需重复 `/api`。

Lab 页面提供 **启动 Lab / 停止 Lab / 查看日志**。启动会先构建当前仓库的 Lab，再用控制台所用的 JDK 启动独立 JVM，使用 `dev` 配置和现有 `bootstrap-local.yml`，端口及 context-path 跟随 `KUMA_LAB_URL`。构建和启动均在后台执行，日志位于 Lab 的 `build/console-runtime/`。构建中也可停止；控制台退出时会停止它启动的 Lab。

停止按钮只控制本控制台启动的进程。已有 IDE 启动的 Lab 或其他程序占用目标端口时，会显示端口占用并禁用启动。仅支持本机 HTTP Lab 地址；WSL 或远程服务继续使用实验请求功能。通常自动从当前目录向上查找仓库；独立安装的桌面程序可设置 `KUMA_CONSOLE_WORKSPACE=D:\KumaProject\KumaFramework` 指定仓库。

WSL 通过 `wsl.exe --distribution … --user … --exec kubectl` 查询，默认 root 能读取 k3s 的 `/etc/rancher/k3s/k3s.yaml`；可以换成已具有 kubeconfig 权限的普通用户。不会启动停止中的发行版。Windows 本机指标不会与 WSL/Linux 指标混合，项目入口可指向 Windows 或 WSL 的实际地址。

控制台仅监听 `127.0.0.1`。k3s 功能只执行查询与日志读取，不提供删除、重启、exec 或任意命令。资源详情隐藏 Secret 数据、常见凭据字段及 kubectl 原始清单。Pod 日志显示服务原始输出。Lab 代理仅接受固定 Lab 地址下的实验和 OpenAPI 路径，实验执行结果以目标服务实际响应为准。

没有开放 Actuator 的项目仍可能显示“HTTP 可达”，这只说明收到 HTTP 响应，不能证明所有依赖正常。需要完整健康状态时，在对应项目开放健康端点并配置好认证。
