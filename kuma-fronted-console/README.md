# Kuma Console 桌面前端

## Yuna 工作空间主题

当前界面参考本机 Yuna 项目的银白发角色设定、冰蓝色配饰与半透明材质，使用冰蓝、石墨灰和淡紫灰统一深浅主题。五个同级页面使用七张场景插画，左侧导航显示各页对应缩略图。点击侧栏「工作空间」旁的箭头可收起整个侧栏：收起后保留图标导航和 Console 配置入口，内容区同步扩展，桌面端折叠状态会保存。窄屏默认使用图标栏，展开时以抽屉显示，点击遮罩、选择页面或按 Esc 可收起。实验搜索保留在 Lab 内。

「服务器与 WSL」合并远程服务器和本机环境，在同一页面通过「远程服务器」「本机 WSL / k3s」切换，页头插画随视图切换。远程资源监控、远程 k3s、WSL 启停、发行版详情和本机 k3s 查询均保留。`#server` 打开远程视图，`#server/wsl` 打开本机视图，旧的 `#cluster` 链接继续可用。环境页签支持方向键及 Home / End 键切换。

「项目与组件」合并项目服务和本地公共组件，在同一页面通过「项目服务」「本地公共组件」切换。保留项目目录、依赖查看、运行进程、服务健康检查和诊断，以及 UAA / Gateway 的启停与日志。`#projects` 打开项目视图，`#projects/components` 打开公共组件视图，旧的 `#components` 链接继续可用；页签同样支持键盘切换，页头插画随视图更新。

左下角独立的「Console 配置」入口位于工作空间分组之外，折叠分组后仍可访问，窄侧栏显示齿轮图标。配置页（`#settings`）集中提供深浅主题、浏览器启动欢迎动画开关、动画预览、Windows 桌面开机自启及当前客户端信息。主题与欢迎动画偏好保存在当前客户端；桌面启动页继续用于展示真实服务启动进度。

顶部标题栏、侧栏、启动页和浏览器页签共用 `renderer/assets/kuma-console.svg`；桌面窗口使用同目录的 ICO / PNG。修改矢量标识后运行 `pnpm run build:icons`，生成 256px PNG 与包含 16–256px 七种尺寸的 Windows ICO，再重建后端。

`renderer/yuna-workspace.js` 统一组织页头和导航图片，保留原有监控数据节点与操作按钮；`renderer/yuna-workspace.css` 提供场景主题，最后加载的 `renderer/design-system.css` 统一字体、卡片、边框、阴影及控件状态。普通卡片使用 16px 圆角和轻阴影，内部容器使用 11px 圆角，控件使用 9px 圆角；深浅主题分别设置边框、阴影和焦点颜色，字体使用本地系统字体，无需联网。`renderer/assets/yuna/` 保存本地插画与生成记录，运行时不依赖 Yuna 项目路径或外部图片服务。Electron 启动页使用同套插画与独立的 `electron/yuna-splash.css`。

服务器状态页通过本机 OpenSSH 每 15 秒读取远程 Linux 的 CPU、内存、磁盘、网络、进程、监听端口、系统服务和容器。默认连接 `root@117.72.13.197`，密钥路径为 `C:/Users/Kuma/Desktop/server/Kumakey.pem`；可用 `KUMA_REMOTE_TARGET`、`KUMA_REMOTE_KEY` 环境变量覆盖。远端需要 Python 3，首次连接须已在本机 SSH known_hosts 中确认主机。密钥只供后端读取，不会传到页面或复制进安装包。连接失败时保留最后一次成功数据并标注过期。

独立的 Electron 模块，提供本机、WSL / k3s、全部项目 / starter 依赖及 Lab 工作台。深色侧栏配浅色内容区，可切换深色主题；主题选择保存在当前客户端。依赖详情使用卡片和逐层路径展示，也保留完整依赖树。

概览只提供通用系统指标和已配置监控项的汇总；具体项目、发行版和集群资源放在对应页面。
前端只依赖监控 API，不写死业务项目名。Windows / WSL 已实际验证；Linux / macOS 已补充 Gradle / Java 启动分支，仍需在目标系统验证打包及系统指标。WSL 功能只在 Windows 显示。

Lab 响应默认把字符串内 `\n`、`\r`、`\t` 按实际换行/缩进展示，JSON 格式可切换，复制保留原始响应。

界面使用 Yuna 风格的七张独立主题插画与统一深浅主题。桌面启动页展示真实的环境检查、服务准备与连接阶段；浏览器首次打开播放可跳过的欢迎动画，重播入口位于「Console 配置 → 预览启动动画」，顶部不显示动画按钮。遵循系统减少动态效果设置。图片保存在项目本地，当前生成方式与提示词见 `renderer/assets/yuna/ARTWORK.md`；历史插画记录保留在 `renderer/assets/ARTWORK.md`。

Lab 顶部提供快速查找：支持「Redis 写入」等多关键词、中英文知识点别名、知识分组，以及全部 / 接口 / 学习 / 收藏 / 最近访问筛选。卡片上的 ☆ 可收藏，收藏和最近 12 次访问只在当前客户端保存。点击搜索框输入关键词；`Enter` 打开首个结果、`Esc` 重置筛选。点击卡片会定位到请求编辑器或运行命令，实验由用户继续执行。

实验目录在桌面和窄屏均默认折叠，点击「浏览目录」展开、点击「收起目录」折叠；折叠时工作台使用整行空间，顶部搜索仍可直接定位实验。学习页按运行和验证步骤组织，教程排版展示标题、代码、表格和列表，代码入口可按需展开。

Lab 目录包含 95 个 HTTP 接口与 45 个本机/Linux 学习实验，界面按实际目录动态统计。内存模型、Webhook、Socket及JDK版本实验展示运行环境、入口、命令、预期结果和完整说明；复制命令后在终端运行，学习实验无需启动Lab服务。OpenAPI同步刷新接口名称与分组，保留学习目录与已有请求示例。

## 打开桌面程序

Windows 安装桌面快捷方式并启用当前账户登录后自动启动：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/install-desktop.ps1
```

之后双击桌面的 `Kuma Console` 即可打开，无需终端。「左下角 Console 配置 → 桌面设置 → 开机自动启动」可查看实际状态并随时开关自启，窗口菜单「设置」也保留相同开关；再次运行安装脚本加 `-DisableAutoStart` 也可关闭。快捷方式和自启引用当前项目目录，移动项目后请重新运行安装脚本。

桌面设置交互验证：`pnpm exec electron scripts/startup-settings-ui-smoke.cjs`，覆盖状态读取、开关保存、失败回退与浏览器模式，不修改真实系统启动项。

Windows 双击本模块的 `start-console.cmd`。首次安装 Electron 依赖，自动连接或构建并启动 `kuma-project-console`，无需手动开浏览器。开发环境需要 Node.js 22.12+、pnpm 11.10.0 和 JDK 25。

```powershell
cd kuma-fronted-console
pnpm install --frozen-lockfile
pnpm start
```

窗口提供原生最小化、最大化、关闭及可拖动标题栏。最小化、隐藏和关闭窗口均收起到右下角系统托盘（可在 Windows 隐藏图标折叠栏中找到），程序和后端继续运行；启动过程中关闭窗口也会在后台继续启动。单击或双击托盘图标、托盘菜单「打开 Kuma Console」或重复启动均可恢复已有窗口。`pnpm start` 会自动构建并启动后端；通过托盘菜单「退出 Kuma Console」或窗口菜单「设置 → 退出」才真正退出，并停止本次启动的后端或取消进行中的启动。如果连接的是此前单独启动的控制台，则保留原服务；可用后端模块的 `stop-console.cmd` 停止它。托盘图标的具体位置由 Windows 管理。

桌面窗口最小化时自动显示置顶的 Yuna 悬浮球，可拖动调整位置，球上显示 CPU 使用率。点击展开「本机概览」，查看 CPU、内存、存储、进程数、运行时间及上下行网速，每 2 秒读取本机监控数据；断线时保留最近数值并提示重试，过期采样也会明确标记。再次点击球、面板右上角 × 或按 Esc 可收起；「打开 Kuma Console」恢复主窗口并隐藏悬浮球。恢复主窗口后停止悬浮球轮询，退出程序时销毁悬浮窗口。此功能仅适用于 Electron 桌面客户端。

退出 Kuma Console 会保留 WSL 及其中的中间件和集群。需要关闭时，在「服务器与 WSL」中手动选择关闭发行版或关闭全部 WSL。

系统托盘验证：`pnpm run test:system-tray`，覆盖关闭、隐藏、最小化驻留托盘，图标和菜单恢复，悬浮球联动，以及显式退出后的窗口和托盘清理。`pnpm test` 使用 JDK 验证结束 Java 控制台后端后，其常驻子会话仍保持运行。

悬浮球验证：`pnpm run test:floating-overview`，使用真实 Electron 窗口与沙箱预加载，模拟监控 API 和鼠标屏幕坐标，覆盖最小化、拖动与点击区分、屏幕边界、数据展示、断线恢复、窗口恢复、隐藏停止轮询及 IPC 隔离。预览截图位于 `build/floating-overview-smoke/`。

`KUMA_CONSOLE_PORT` 设置本机服务端口（默认 18090）；其他监控配置仍由后端 `application.yml` 及对应环境变量控制。

## 目录与构建

- `renderer/`：HTML、CSS、JavaScript 与离线 Lab 接口目录；Gradle 将这些文件打进后端 JAR 的 `static/`，浏览器和桌面共用同一份界面。
- `electron/`：桌面窗口、启动页与后端生命周期。渲染进程启用 sandbox / contextIsolation，不暴露 Node API；项目链接用系统浏览器打开。
- `scripts/`：启动和后端构建入口。

```powershell
pnpm run check       # JavaScript 语法检查
pnpm test            # 响应展示和实验目录检查
pnpm run test:lab-ui # 独立浏览器交互、目录同步及窄屏检查（使用模拟接口）
pnpm run pack        # 构建后端及 dist/win-unpacked/Kuma Console.exe
pnpm run dist        # 构建便携 EXE，包含后端 JAR
```

打包后的程序需要本机 PATH 上的 JDK 25，无需 Node.js / pnpm。首次运行自动启动内置 JAR；关闭窗口后驻留系统托盘，选择「退出 Kuma Console」时停止该程序自己启动的后端。如果复用已运行的控制台，则保留原服务。构建产物没有数字签名。

桌面验证：后端启动后运行 `pnpm exec electron . --smoke`。结果和四个页面、依赖详情截图保存在 `build/`，不会执行 Lab 写入实验。

修改前端后，停止并重新启动后端，或者运行 `pnpm run prepare:backend` 后重启服务。更新离线 Lab 目录继续使用后端的 `scripts/refresh-lab-catalog.py`，生成文件写入本模块的 `renderer/`。

学习目录与说明同步：运行 `python kuma-project/kuma-project-console/scripts/refresh-lab-learning.py`（从仓库根目录）。脚本检查源码路径及JDK版本表，生成 `lab-learning.json` 和 `lab-guides/`；正常使用界面无需Python。

项目目录默认筛选「已启动」：发现匹配的本机项目进程，或服务探测获得 HTTP 响应（包括健康检查异常），即纳入列表；Gradle 构建启用状态不作为启动依据。计数随运行状态刷新，探测失败时不使用对应来源的旧数据判断已启动。服务可达与健康检查通过分别展示，后端每 10 秒探测一次。
