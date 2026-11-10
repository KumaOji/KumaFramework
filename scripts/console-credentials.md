# Console 本机凭据读取

在仓库根目录运行，默认读取已保存的凭据并隐藏密码、API Key 和临时登录链接：

```powershell
.\scripts\get-console-credentials.ps1 | Format-Table service,status,username,password,source -AutoSize
.\scripts\get-console-credentials.ps1 -Service grafana,qinglong -Reveal
.\scripts\get-console-credentials.ps1 -Reveal -AsJson
```

只有显式添加 `-Reveal` 才返回明文。命令不启动 WSL、不修改账号或重置密码；也不读取 dsh/n8n 的模型 API Key。没有开放返回全部密码的 HTTP 接口。

Console 的「Console 配置 → 本机账号与凭据」提供相同读取能力，浏览器版和桌面版共用。先点击「读取 / 刷新」获得脱敏列表，再按服务显示、复制凭据；60 秒、窗口失焦或离开页面时隐藏。`POST /api/credentials` 仅允许本机同源且携带专用请求头的请求，响应使用 `no-store`，拒绝一次性显示所有服务的密码。需要 Windows 及本机仓库脚本；打包桌面版通过 `KUMA_CONSOLE_WORKSPACE` 指向仓库目录。

自动来源包括 Grafana 的 WSL 密码文件、青龙的 WSL 凭据文件，以及现有 k3s Secret 中的 n8n Basic Auth、Qdrant、MySQL、PostgreSQL、Redis 配置。dsh 返回本机启动日志中的登录链接，原进程停止后链接可能失效。n8n Basic Auth 配置与应用内 owner 登录并非同一账号。

`available` 表示读取到了已保存的值，不代表已执行登录验证；密码在界面修改后，旧配置可能不再有效。`manual-required` 表示需补充已知账号，`unavailable` 表示当前环境无法读取来源，`no-stored-login` 表示此目录没有记录独立登录密码。

## 私有补充文件

本机文件默认位于 `C:\Users\Kuma\.kuma-console\credentials.json`，代码根据实际当前用户目录定位。文件已在本机初始化，内容不会存放到仓库中。

```powershell
.\scripts\get-console-credentials.ps1 -Initialize
notepad "$env:USERPROFILE\.kuma-console\credentials.json"
```

在本机编辑该文件，按 service id 填写自己已知的密码；以下只展示格式，不包含真实凭据：

```json
{
  "version": 1,
  "services": {
    "nacos": { "username": "填写用户名", "password": "填写密码" },
    "n8n": { "username": "填写登录邮箱", "password": "填写密码" },
    "uaa": { "username": "填写用户名", "password": "填写密码" },
    "blog": { "username": "填写用户名", "password": "填写密码" }
  }
}
```

私有文件优先于自动来源，也支持新增任意 service id。字段可用 `username`、`password`、`apiKey`、`loginUrl`。应用数据库中仅保留的密码哈希不能反查成原密码，因此不会把数据库连接密码、Nacos 服务端 token 或 n8n 加密密钥当作界面登录密码。

`-Initialize` 为文件设置仅当前 Windows 用户和 SYSTEM 可访问的 ACL。`-PrivateFile` 可指定其他路径，但脚本拒绝把私有文件放进当前仓库或任何其他 Git 工作区。Git 忽略规则同时覆盖常见的本机凭据副本命名；不要把明文输出另存成仓库中的普通文件。

验证：`powershell -NoProfile -ExecutionPolicy Bypass -File scripts/console-credentials.test.ps1`。测试只使用临时假凭据。
