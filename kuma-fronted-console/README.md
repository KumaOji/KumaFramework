# Kuma Console 桌面前端

独立的 Electron 模块，提供本机、WSL / k3s、全部项目 / starter 依赖及 Lab 工作台。深色侧栏配浅色内容区，可切换深色主题；主题选择保存在当前客户端。依赖详情使用卡片和逐层路径展示，也保留完整依赖树。

概览只提供通用系统指标和已配置监控项的汇总；具体项目、发行版和集群资源放在对应页面。
前端只依赖监控 API，不写死业务项目名。Windows / WSL 已实际验证；Linux / macOS 已补充 Gradle / Java 启动分支，仍需在目标系统验证打包及系统指标。WSL 功能只在 Windows 显示。

Lab 响应默认把字符串内 `\n`、`\r`、`\t` 按实际换行/缩进展示，JSON 格式可切换，复制保留原始响应。

## 打开桌面程序

Windows 双击本模块的 `start-console.cmd`。首次安装 Electron 依赖，自动连接或构建并启动 `kuma-project-console`，无需手动开浏览器。开发环境需要 Node.js 22.12+、pnpm 11.10.0 和 JDK 25。

```powershell
cd kuma-fronted-console
pnpm install --frozen-lockfile
pnpm start
```

窗口提供原生最小化、最大化、关闭及可拖动标题栏。重复启动聚焦已有窗口。关闭桌面窗口后，开发环境后台服务继续运行，便于网页访问；需要停止时双击后端模块的 `stop-console.cmd`。

`KUMA_CONSOLE_PORT` 设置本机服务端口（默认 18090）；其他监控配置仍由后端 `application.yml` 及对应环境变量控制。

## 目录与构建

- `renderer/`：HTML、CSS、JavaScript 与离线 Lab 接口目录；Gradle 将这些文件打进后端 JAR 的 `static/`，浏览器和桌面共用同一份界面。
- `electron/`：桌面窗口、启动页与后端生命周期。渲染进程启用 sandbox / contextIsolation，不暴露 Node API；项目链接用系统浏览器打开。
- `scripts/`：启动和后端构建入口。

```powershell
pnpm run check       # JavaScript 语法检查
pnpm run pack        # 构建后端及 dist/win-unpacked/Kuma Console.exe
pnpm run dist        # 构建便携 EXE，包含后端 JAR
```

打包后的程序需要本机 PATH 上的 JDK 25，无需 Node.js / pnpm。首次运行自动启动内置 JAR；关闭时停止该程序自己启动的后端。如果复用已运行的控制台，则保留原服务。构建产物没有数字签名。

桌面验证：后端启动后运行 `pnpm exec electron . --smoke`。结果和四个页面、依赖详情截图保存在 `build/`，不会执行 Lab 写入实验。

修改前端后，停止并重新启动后端，或者运行 `pnpm run prepare:backend` 后重启服务。更新离线 Lab 目录继续使用后端的 `scripts/refresh-lab-catalog.py`，生成文件写入本模块的 `renderer/`。
