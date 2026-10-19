# Elasticsearch 实验

使用现有 WSL K3s `base/skywalking-storage`（Elasticsearch 8.17.3），不需要启动 Spring 或 Nacos。
Java HttpClient 直接调用 REST API，报告包含每一步的 explanation、method、path、请求 DSL、HTTP status、服务端 response 与 checks。
这些实验不依赖 Elasticsearch Starter 自动配置；目的是先看懂实际请求和响应。

## 运行

从仓库根目录执行（JDK25、WSL Ubuntu-24.04、Gradle）：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-elasticsearch-labs-wsl.ps1
```

脚本临时转发 ES 到 Windows `127.0.0.1:19200`，完成后关闭自己创建的转发。
冷启动时等待 Pod 就绪，并在启动窗口内重试转发；超过55秒仍不可达则失败。
添加 `-Lesson crud`、`-Lesson search`、`-Lesson aggregation` 可单独学习；`-Port 19201` 可避开端口冲突。
日志保存在 `kuma-project/kuma-project-lab/build/es-lab/`。

如已有可访问的 ES，直接运行：

```powershell
$env:KUMA_LAB_ES_URL = 'http://127.0.0.1:19200'
./gradlew.bat :kuma-project:kuma-project-lab:elasticsearchLab -PesLesson=search
```

默认地址是 `http://127.0.0.1:19200`。当前 WSL ES 没有开启鉴权；此实验仅支持无鉴权 HTTP(S) origin。
K3s 内运行 Lab 时可设置 `KUMA_LAB_ES_URL=http://skywalking-storage.base.svc.cluster.local:9200`。
仓库的 `k8s/base/lab.yaml` 已配置集群内地址，下次部署时生效。

## 索引与文档 CRUD

每次生成 `kuma_lab_es_<随机UUID>` 索引，单分片、零副本、`dynamic=strict`，显式定义：

| 字段 | 类型 | 用途 |
|---|---|---|
| title | text，standard analyzer | 全文搜索与高亮 |
| category | keyword | 精确过滤和分组聚合 |
| price / stock | integer | 数值范围、排序、统计 |

先读取 Mapping，再通过 NDJSON `_bulk` 写入三条样本：

| ID | title | category | price | stock |
|---|---|---|---|---|
| 1 | Elasticsearch search basics | search | 20 | 8 |
| 2 | Advanced Elasticsearch search | search | 40 | 4 |
| 3 | PostgreSQL database transactions | database | 90 | 2 |

HTTP 200 不等于 bulk 每项成功，实验同时检查 `errors=false` 与三项结果。
为了稳定演示近实时搜索，仅本次索引关闭自动 refresh：GET 文档立即查到，但 `_count=0`；
对该索引执行 `_refresh` 后 `_count=3`。这不是模拟结果，检查值来自服务端。
然后 `_update` 将 ID1 的 price 改成25，验证 stock 仍为8；删除 ID3，GET 返回404且 found=false，搜索计数变成2。
更新、删除使用 `refresh=true` 让这组小型实验立即验证；生产中不要据此频繁强制 refresh。

## 分词与搜索

1. `_analyze` 将 `Elasticsearch Search` 分成 `elasticsearch`、`search`。
2. `match title=Elasticsearch` 会分析查询字符串，命中2条。
3. `term title=Elasticsearch` 不分析查询字符串，命中0条；改为小写 `elasticsearch` 命中2条。
4. `bool.must` 匹配 title 的 `search`，`filter` 精确匹配 category 且 price≥30，按 price 升序并高亮，只命中 ID2。

从报告复制实际 DSL，用 `POST /<本次索引>/_search` 理解结构；报告返回时索引已清理，若要手动复现，
在自己的新实验索引中按报告的建索引、bulk、refresh、search 请求顺序执行，最后删除该索引。
standard 是通用分词器，本实验使用英文样本，不代表安装了中文 IK 分词插件。

## 分组与聚合

在更新和删除前，对原始三条样本聚合：category 的 terms 分桶得到 search=2、database=1；
每组 avg(price) 分别为30、90。全局 stats：count=3、sum=150、avg=50、min=20、max=90。
`size=0` 不返回文档，只返回聚合结果；在 keyword 上聚合，避免对 text 启用 fielddata。
单分片小样本用于学习，不验证大规模分布式 terms 误差、分页或性能。

## 控制台与 HTTP

控制台的学习卡片支持完整实验和三个专项，实验接口为：

| 方法 | 路径 | 用途 |
|---|---|---|
| GET | /api/lab/elasticsearch/topics | 学习目录 |
| POST | /api/lab/elasticsearch/scenario | 完整实验 |
| POST | /api/lab/elasticsearch/crud | 索引与文档 CRUD |
| POST | /api/lab/elasticsearch/search | 分词与搜索 |
| POST | /api/lab/elasticsearch/aggregation | 分组与统计 |

接口无需请求体，Lab 进程需自行配置可达的 `KUMA_LAB_ES_URL`。脚本的临时转发只服务本次命令，
不会为已运行的 Lab 建立持久转发。Windows Lab 可在另一终端保持运行：

```powershell
wsl -d Ubuntu-24.04 -u root -- kubectl -n base port-forward --address 127.0.0.1 service/skywalking-storage 19200:9200
```

每个专项都会创建自己的三条样本，互不依赖。正常完成或检查失败后都尝试删除本次索引；
若连接中断导致清理失败，错误会保留索引名，恢复后按该精确名称清理。
服务不可用、HTTP 错误、搜索超时/分片失败、实际结果不符都会使实验失败。
只对生成的独立索引读写，不使用通配符清理，不读写 SkyWalking 索引。

## 验证

```powershell
# 离线：验证异常路径清理和 endpoint 校验
./gradlew.bat :kuma-project:kuma-project-lab:test --tests '*Elasticsearch*Test'
# 真实 WSL：完整实验及三个专项，每次清理自己创建的索引
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-elasticsearch-labs-wsl.ps1 -Test
```

真实集成测试仅在显式设置 `KUMA_LAB_ES_URL` 时运行。

参考：[Refresh API](https://www.elastic.co/docs/api/doc/elasticsearch/v8/operation/operation-indices-refresh)、
[Terms aggregation](https://www.elastic.co/guide/en/elasticsearch/reference/8.19/search-aggregations-bucket-terms-aggregation.html)。
