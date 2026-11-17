"""Generate standalone lesson cards and docs for the shared renderer (Python 3.10+)."""
import json
import re
import shutil
from pathlib import Path

root = Path(__file__).resolve().parents[3]
lab = root / 'kuma-project/kuma-project-lab'
renderer = root / 'kuma-fronted-console/renderer'
java = 'src/main/java/com/kuma/cloud/lab'
ps = 'powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-jdk-version-labs.ps1'
cards = []


def jdk_commands(packages, main, folder):
    paths = ','.join(f'"$lab/src/main/java/com/kuma/cloud/lab/{package}"' for package in packages)
    return '\n'.join([
        "$lab = 'kuma-project/kuma-project-lab'",
        f'New-Item -ItemType Directory -Force "$lab/build/{folder}/classes" | Out-Null',
        f"$sources = Get-ChildItem {paths} -Filter '*.java' | ForEach-Object FullName",
        f'javac --release 25 -encoding UTF-8 -d "$lab/build/{folder}/classes" $sources',
        f'java -cp "$lab/build/{folder}/classes" com.kuma.cloud.lab.{main}',
    ])


def add(identifier, group, name, description, runtime, commands, topics, expected, sources, doc, main=''):
    for path in [*sources, doc]:
        if not (lab / path).is_file():
            raise ValueError(f'Missing lesson source: {path}')
    cards.append(dict(kind='learning', id=identifier, group=group, name=name,
                      description=description, runtime=runtime, commands=commands,
                      topics=topics, expected=expected,
                      sources=[f'kuma-project/kuma-project-lab/{path}' for path in sources],
                      doc=f'/lab-guides/{Path(doc).name}', mainClass=main))


add('lock', 'Java 并发与内存', '并发与锁实验', '可见性、原子性、CAS、锁与任务完成通知。', 'JDK 25 · 本机',
    jdk_commands(['lock'], 'lock.LockLearningDemo', 'lock-lab'),
    ['volatile', 'CAS / ABA', 'ReentrantLock', 'AQS', 'CountDownLatch'],
    ['全部五组并发检查通过；示例用于验证同步语义。'], [f'{java}/lock/LockLearningDemo.java'], 'docs/memory-labs.md',
    'com.kuma.cloud.lab.lock.LockLearningDemo')
add('memory', 'Java 并发与内存', 'JMM 与 JVM 内存区域', '运行锁实验、重排序统计、发布验证与内存区域观察。', 'JDK 25 · 本机',
    jdk_commands(['lock', 'memory'], 'memory.MemoryLearningDemo', 'memory-lab'),
    ['happens-before', 'Store buffering', 'VarHandle', '堆 / 线程栈 / 元空间', '堆外缓冲区'],
    ['volatile 对照中 (0,0) 为零；release/acquire 发布通过。', '线程局部值与直接缓冲区检查通过；普通字段不保证出现 (0,0)。'],
    [f'{java}/memory/MemoryLearningDemo.java', f'{java}/memory/JmmLesson.java', f'{java}/memory/JvmMemoryLesson.java'],
    'docs/memory-labs.md', 'com.kuma.cloud.lab.memory.MemoryLearningDemo')
for identifier, name, suffix, topics, expected in [
    ('linux-memory', 'Linux 用户态与内核空间', '', ['系统调用', 'CPU user/system 时间', 'mmap', '缺页', '写时复制', 'EFAULT'],
     ['映射读写与写时复制通过；PROT_NONE 缓冲区返回 EFAULT，恢复权限后成功。', 'CPU 时间和缺页数量为观察值，不断言固定数值。']),
    ('linux-trace', 'Linux 系统调用跟踪', ' trace', ['strace', 'getpid', 'mprotect', 'pread64'],
     ['查看 getpid 汇总和内存系统调用；strace 跟踪耗时不用于性能比较。']),
]:
    add(identifier, 'Linux 内存与系统调用', name, '在 Linux 或 WSL 终端运行，从仓库根目录执行。',
        'Linux / WSL · gcc' + (' · strace' if suffix else ''),
        'bash kuma-project/kuma-project-lab/scripts/run-linux-memory-lab.sh' + suffix,
        topics, expected, ['src/main/c/linux_memory_lab.c', 'scripts/run-linux-memory-lab.sh'], 'docs/memory-labs.md')
for identifier, name, main, topics, expected in [
    ('network', 'Webhook 与 Socket 完整实验', 'NetworkLearningDemo', ['Webhook', 'TCP', 'UDP'], ['Webhook、TCP 和 UDP 全部检查通过。']),
    ('webhook', 'Webhook 签名、幂等与重试', 'WebhookLesson', ['HMAC-SHA256', '篡改与过期拒绝', '并发去重', '503 重试'],
     ['重复事件仅一份模拟业务效果；重试首次503、第二次202。', '本例为自定义示例签名协议，事件表仅在进程内保存。']),
    ('socket', 'TCP 分帧与 UDP 通信', 'SocketLesson', ['长度前缀', '半关闭', 'EOF', '读取超时', 'UDP 数据报'],
     ['三个 TCP 帧内容正确；非法帧被拒绝；超时后连接仍可用。', '两个 UDP 数据报完整回显；回环成功不代表公网可靠性。']),
]:
    add(identifier, '网络通信实验', name, '使用回环地址和临时端口，无需 Lab 服务或外部系统。', 'JDK 25 · 本机',
        jdk_commands(['network'], f'network.{main}', 'network-lab'), topics, expected,
        [f'{java}/network/{main}.java'], 'docs/network-labs.md', f'com.kuma.cloud.lab.network.{main}')

versions = {
    8: ('Lambda、Stream 与异步任务', 'EarlyJdkLessons', ['Optional', 'java.time', 'CompletableFuture']),
    9: ('集合工厂、Stream 前缀与模块', 'EarlyJdkLessons', ['List.of', 'takeWhile / dropWhile', 'java.base']),
    10: ('var 与集合快照', 'EarlyJdkLessons', ['类型推断', 'List.copyOf']),
    11: ('String 与 Files 新 API', 'EarlyJdkLessons', ['isBlank / strip / repeat / lines', 'readString / writeString', 'HTTP Client 在18组合验证']),
    12: ('teeing 双路收集', 'EarlyJdkLessons', ['Collectors.teeing', '数量与总和']),
    14: ('switch 表达式', 'LanguageLessons', ['箭头分支', 'yield']),
    15: ('Text blocks 文本块', 'LanguageLessons', ['公共缩进', '末尾换行']),
    16: ('record 与 instanceof 模式', 'LanguageLessons', ['值相等', '紧凑构造器', 'Stream.toList']),
    17: ('sealed 继承边界', 'LanguageLessons', ['permitted subclasses', '反射元数据']),
    18: ('简单文件服务器', 'RecentJdkLessons', ['SimpleFileServer', 'HTTP Client', '200 / 404']),
    21: ('虚拟线程与模式匹配', 'RecentJdkLessons', ['Sequenced Collections', 'record patterns', '穷举 switch']),
    22: ('外部内存与未命名模式', 'RecentJdkLessons', ['Arena', '越界 / 线程 / 生命周期', 'FFM', '_']),
    24: ('Gatherers 与字节码 API', 'RecentJdkLessons', ['分窗 / scan', 'Class-File 生成与执行']),
    25: ('ScopedValue 与新语法', 'Jdk25Lesson', ['module import', '灵活构造器', '作用域隔离']),
}
entry_source = lab / f'{java}/jdk/JdkVersionLearningDemo.java'
declared = re.search(r'VERSIONS = List.of\(([^)]+)\)', entry_source.read_text(encoding='utf-8'))
if not declared or {int(v.strip()) for v in declared[1].split(',')} != set(versions):
    raise ValueError('JDK lesson version map is out of sync with VERSIONS')
add('jdk-all', 'JDK 版本功能', 'JDK 8～25 全部代表性功能', '版本标签表示正式引入版本，统一使用 JDK25 执行。', 'JDK 25 · 正式功能',
    ps, [f'JDK {v}' for v in versions], ['14个版本分组及紧凑源文件检查通过。'],
    [f'{java}/jdk/JdkVersionLearningDemo.java', 'scripts/run-jdk-version-labs.ps1'], 'docs/jdk-version-labs.md',
    'com.kuma.cloud.lab.jdk.JdkVersionLearningDemo')
for version, (name, source, topics) in versions.items():
    add(f'jdk-{version}', 'JDK 版本功能', f'JDK {version} · {name}', '在 JDK25 上体验该正式版本的代表性功能，并检查结果。',
        'JDK 25 · 正式功能', ps + f' -Version {version}', topics, ['所选版本的功能检查通过；版本号不是本机 JDK 的切换指令。'],
        [f'{java}/jdk/{source}.java'], 'docs/jdk-version-labs.md', 'com.kuma.cloud.lab.jdk.JdkVersionLearningDemo')
    cards[-1]['mainArgs'] = str(version)
add('jdk25-compact', 'JDK 25 独立示例', '紧凑源文件与实例 main', '直接运行不含显式 class 的源文件。', 'JDK 25 · 正式功能',
    'java --source 25 kuma-project/kuma-project-lab/examples/jdk25/CompactMain.java', ['IO.println', '实例 main', '紧凑源文件'],
    ['sum=6 passed。'], ['examples/jdk25/CompactMain.java'], 'docs/jdk-version-labs.md')
add('jdk25-preview', 'JDK 25 独立示例', '原始类型模式与 switch（预览）', '独立编译运行，脚本为编译及运行启用预览开关。',
    'JDK 25 · 预览功能', ps + ' -Version 25 -IncludePreview', ['primitive patterns', '精确窄化', '--enable-preview'],
    ['127 匹配 byte、128 不匹配；primitive switch 通过。', '预览编译警告为预期输出。'],
    ['examples/jdk25-preview/PrimitivePatterns.java'], 'docs/jdk-version-labs.md')

for identifier, name, description, command, runtime, topics, expected, sources, main in [
    ('otel-sdk', 'OTel SDK：信号、传播与采样', '真实 SDK 输出 span 树、异步断链对照、采样、指标和关联日志。',
     './gradlew.bat :kuma-project:kuma-project-lab:otelLab', 'JDK 25 · Gradle · 无需后端',
     ['trace / span', 'W3C traceparent', 'Context.wrap', '采样', 'counter / histogram', '日志关联'],
     ['7个 span，其中6个属于同一 trace；所有 checks 为 true。', 'metrics/logs 仅由本地 exporter 捕获。'],
     [f'{java}/observability/OtelLearningDemo.java'], 'com.kuma.cloud.lab.observability.OtelLearningDemo'),
    ('otel-skywalking', 'OTel → Collector → SkyWalking 查回', '导出真实 OTLP trace，逐个验证同一 trace 的六个 span 已存储。',
     './gradlew.bat :kuma-project:kuma-project-lab:otelLab -PotelExport=true', 'JDK 25 · Gradle · Collector/OAP',
     ['OTLP', 'receiver / processor / exporter', 'Zipkin / Lens', '存储验证'],
     ['backendStatus=VERIFIED_IN_SKYWALKING。', 'UI 使用 Zipkin/Lens 查询报告 traceId；不是原生 Agent 服务页面。'],
     [f'{java}/observability/OtelLearningDemo.java'], 'com.kuma.cloud.lab.observability.OtelLearningDemo'),
    ('skywalking-agent', 'SkyWalking Agent：正常、慢调用与异常', '先运行无 Agent 对照，再按教程挂载 Agent，查询原生自定义 span。',
     './gradlew.bat :kuma-project:kuma-project-lab:skywalkingLab', 'JDK 25 · Gradle · 可选 Java Agent',
     ['@Trace', 'ActiveSpan', 'TraceContext', '慢调用定位', '异常标记', '原生 APM'],
     ['无 Agent 时 activeTrace=false；三个业务模拟正常完成。', '挂载兼容 Agent 后产生 native ID；上报与存储需在原生 UI 验证。'],
     [f'{java}/observability/SkyWalkingLearningDemo.java', f'{java}/observability/ObservabilityLabController.java'],
     'com.kuma.cloud.lab.observability.SkyWalkingLearningDemo'),
]:
    add(identifier, 'OTel 与 SkyWalking 实验', name, description, runtime, command, topics, expected,
        sources, 'docs/observability-labs.md', main)
    if identifier == 'otel-skywalking':
        cards[-1]['mainArgs'] = '--export http://localhost:4318/v1/traces http://localhost:9412'

docs = renderer / 'lab-guides'
database_script = 'powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-observability-database-wsl.ps1'
for identifier, name, suffix, topics, expected in [
    ('database-otel', '真实 PostgreSQL/MySQL + OTel SQL trace', ' -Export',
     ['JDBC', 'SQL span', '数据库端慢查询', '重复键约束', '事务回滚', '独立 schema'],
     ['正常/慢查询提交后库存9、支付1；约束失败回滚后库存10、支付0。', '所有SQL span从SkyWalking查回；每个runId的数据库行保留供对照。']),
    ('database-skywalking', '真实 JDBC + SkyWalking Agent', ' -Telemetry skywalking',
     ['JDBC 自动采集', 'PreparedStatement', 'commit / rollback', '数据库依赖', '慢 SQL', 'SQLSTATE'],
     ['真实SQL与事务验证通过，产生native trace ID。', '原生UI查询JDBC span、慢SQL和重复键错误；MySQL可加 -Database mysql。']),
]:
    add(identifier, 'OTel 与 SkyWalking 真实数据库实验', name,
        '默认使用现有WSL PostgreSQL；独立实验命名空间，每次按runId隔离，不清空历史结果。',
        'JDK 25 · Gradle · WSL真实数据库 · Collector/OAP', database_script + suffix, topics, expected,
        [f'{java}/observability/DatabaseObservabilityLearningDemo.java', 'scripts/run-observability-database-wsl.ps1'],
        'docs/observability-labs.md', 'com.kuma.cloud.lab.observability.DatabaseObservabilityLearningDemo')

monitoring_script = 'powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-monitoring-labs-wsl.ps1'
for component, name, topics, expected, ui in [
    ('loki', 'Loki：日志写入与 LogQL 查回', ['日志流', '标签', 'runId 正文过滤', 'Grafana Explore'],
     ['写入返回204；本次runId的日志可查回。'],
     [('Grafana Explore / Loki', 'http://localhost:3000/explore', 'Explore → Loki，粘贴报告中的 logql')]),
    ('prometheus', 'Prometheus：即时与范围查询', ['PromQL', 'up', 'vector / matrix', 'counter / rate', 'Targets'],
     ['返回当前样本与一分钟历史序列；up=0保留为失联证据。'],
     [('Prometheus', 'http://localhost:9090', '查询页输入 up；Targets 查看采集目标')]),
    ('alertmanager', 'Alertmanager：一分钟实验告警', ['labels / annotations', 'startsAt / endsAt', '告警查回', '接收器边界'],
     ['KumaLabAlert 可按run_id查回，约一分钟后过期；本地不发送外部通知。'],
     [('Alertmanager', 'http://localhost:9093', 'Alerts 筛选 alertname=KumaLabAlert；Silences 查看静默')]),
    ('otel', 'OTel Collector：导出与存储证据', ['OTLP', 'receiver / processor / exporter', '六个关联 span', '查回验证'],
     ['真实SDK输出7个span，其中6个关联span从SkyWalking逐个查回。', 'Collector没有业务UI；metrics/logs仍为SDK内存实验。'],
     [('SkyWalking', 'http://localhost:12880', 'Zipkin Trace / Lens 查报告 traceId；Collector本身无业务UI')]),
    ('skywalking', 'SkyWalking：traceId 与 span 树', ['Zipkin / Lens', '父子 span', '慢调用', '异常标记', '原生Agent对照'],
     ['报告返回真实traceId与已存储span列表，可在Zipkin/Lens对照父子关系。'],
     [('SkyWalking', 'http://localhost:12880', 'Zipkin Trace / Lens 查 OTLP；已有 Agent 实验在原生 Trace 页面')]),
    ('grafana', 'Grafana：面板与数据源检查', ['Dashboards', 'Kuma Services', '数据源健康', '日志与调用链关联'],
     ['现有本地凭据验证三服务面板及Prometheus/Loki/SkyWalking数据源。', '报告不输出凭据。'],
     [('三服务面板', 'http://localhost:3000/d/kuma-services', 'Dashboards → Kuma → Kuma Services'),
      ('Grafana Explore', 'http://localhost:3000/explore', '选择 Loki / Prometheus / SkyWalking traces 数据源')]),
]:
    add('middleware-' + component, '可观测性中间件实验', name,
        '连接真实WSL组件，返回可查回证据、检查结果与UI位置；无需启动LabApplication。',
        'JDK 25 · Gradle · WSL中间件', monitoring_script + ' -Component ' + component,
        topics, expected, [f'{java}/observability/MonitoringMiddlewareLabs.java', 'scripts/run-monitoring-labs-wsl.ps1'],
        'docs/monitoring-middleware-labs.md', 'com.kuma.cloud.lab.observability.MonitoringMiddlewareLabs')
    cards[-1]['mainArgs'] = component
    cards[-1]['uiLinks'] = [dict(name=name, url=url, location=location) for name, url, location in ui]

es_script = 'powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-elasticsearch-labs-wsl.ps1'
for lesson, name, topics in [
    ('all', 'Elasticsearch 完整实验', ['Mapping', 'CRUD', 'refresh', '全文搜索', '聚合']),
    ('crud', 'Elasticsearch 索引与文档 CRUD', ['text / keyword', 'bulk NDJSON', '实时 GET', 'refresh', '部分更新', '删除']),
    ('search', 'Elasticsearch 分词与全文搜索', ['standard analyzer', 'match / term', 'bool / filter / range', '排序', '高亮']),
    ('aggregation', 'Elasticsearch 分组与价格统计', ['keyword', 'terms 分桶', 'avg', 'stats', 'size=0']),
]:
    add('elasticsearch-' + lesson, 'Elasticsearch 实验', name,
        '连接现有 WSL ES，逐步展示请求 DSL、实际响应和 checks；独立索引结束后自动清理。',
        'JDK 25 · Gradle · WSL Elasticsearch 8.17', es_script + ' -Lesson ' + lesson,
        topics, ['所有 checks 为 true，最后 indexCleaned=true。', '每个专项独立建样本，无需启动 Spring 或 Nacos。'],
        [f'{java}/elasticsearch/ElasticsearchLearningDemo.java', 'scripts/run-elasticsearch-labs-wsl.ps1', 'scripts/forward-elasticsearch-lab.sh'],
        'docs/elasticsearch-labs.md', 'com.kuma.cloud.lab.elasticsearch.ElasticsearchLearningDemo')
    cards[-1]['mainArgs'] = lesson

docs.mkdir(exist_ok=True)
for identifier, name, task, args, topics, expected, source in [
    ('socket-all', 'Socket 完整实验：协议与连接生命周期', 'socketLab', '',
     ['TCP 分帧', 'UDP', 'SO_TIMEOUT', '长连接', '半关闭 / EOF', '重连'],
     ['既有分帧、读取超时和UDP检查通过；两个真实TCP连接的生命周期检查全通过。'], 'network/SocketLifecycleLesson.java'),
    ('socket-lifecycle', 'Socket 长连接、半关闭与重连', 'socketLab', ' -PsocketLesson=lifecycle',
     ['连接端点', '连续收发', '连接内状态', 'shutdownOutput', 'isConnected / isClosed'],
     ['首连接三条回复序号1～3，重连后的序号从1开始；关闭后不能复用Socket。'], 'network/SocketLifecycleLesson.java'),
    ('session-all', 'HTTP Session 完整实验', 'sessionLab', '',
     ['CookieManager', 'HttpSession', 'TCP连接与会话', '客户端隔离', 'changeSessionId', 'invalidate', '空闲过期'],
     ['Cookie、隔离、ID更换、注销和空闲过期的checks全为true；容器与临时文件已清理。'], 'session/SessionLearningDemo.java'),
    ('session-isolation', 'Session Cookie 延续与客户端隔离', 'sessionLab', ' -PsessionLesson=isolation',
     ['Cookie / Session', 'isNew', 'getSession(false)', '不同客户端', '同Cookie跨TCP连接'],
     ['A的计数1→2→3，B为1；A换TCP连接仍保留Session，无Cookie的访问产生新Session。'], 'session/SessionLearningDemo.java'),
    ('session-lifecycle', 'Session ID 更换、注销与重建', 'sessionLab', ' -PsessionLesson=lifecycle',
     ['changeSessionId', '属性保留', '旧ID重放', 'invalidate', 'Cookie清除'],
     ['更换ID保留属性，旧ID失效；注销后不能重放恢复，重建计数为1。'], 'session/SessionLearningDemo.java'),
    ('session-expiry', 'Session 空闲超时与过期', 'sessionLab', ' -PsessionLesson=expiry',
     ['maxInactiveInterval', '空闲有效期', 'Cookie有效期', '会话重建'],
     ['无请求等待2.1秒后1秒空闲Session失效，新Session使用新ID和初始计数。'], 'session/SessionLearningDemo.java'),
]:
    add(identifier, 'Socket 与 HTTP Session', name,
        '本机真实Socket / Servlet容器，返回逐步证据与checks；无需Nacos、WSL或数据库。',
        'JDK 25 · Gradle · 本机回环', './gradlew.bat :kuma-project:kuma-project-lab:' + task + args,
        topics, expected, [f'{java}/{source}'], 'docs/socket-session-labs.md',
        'com.kuma.cloud.lab.' + ('network.SocketLifecycleLesson' if task == 'socketLab' else 'session.SessionLearningDemo'))
    cards[-1]['mainArgs'] = 'lifecycle' if identifier == 'socket-lifecycle' else identifier.removeprefix('session-') if identifier.startswith('session-') else 'all'


# Existing audit lessons are generated here as well, so refresh preserves their cards.
cards.extend([{'kind': 'learning',
  'id': 'audit-log-all',
  'group': '日志审计实验',
  'name': '日志审计完整实验',
  'description': '复用真实 audit-log starter 和 Spring AOP，逐步返回审计证据与检查结果，每次运行隔离。',
  'runtime': 'JDK 25 · Spring AOP · 无外部中间件',
  'commands': './gradlew.bat :kuma-project:kuma-project-lab:auditLogLab',
  'topics': ['AuditLog', 'SpEL', '操作人', '成功/失败', '异常传播'],
  'expected': ['5条实际审计快照，全部checks通过；业务失败与存储故障均有明确解释。'],
  'sources': ['kuma-project/kuma-project-lab/src/main/java/com/kuma/cloud/lab/auditlog/AuditLogLearningDemo.java'],
  'doc': '/lab-guides/audit-log-labs.md',
  'mainClass': 'com.kuma.cloud.lab.auditlog.AuditLogLearningDemo',
  'mainArgs': 'all'},
 {'kind': 'learning',
  'id': 'audit-log-content',
  'group': '日志审计实验',
  'name': '审计敏感内容策略与截断',
  'description': '复用真实 audit-log starter 和 Spring AOP，逐步返回审计证据与检查结果，每次运行隔离。',
  'runtime': 'JDK 25 · Spring AOP · 无外部中间件',
  'commands': './gradlew.bat :kuma-project:kuma-project-lab:auditLogLab -PauditLesson=content',
  'topics': ['recordParams', 'recordResult', '敏感信息', '长度截断'],
  'expected': ['示例密钥不出现在审计入参/出参，400字符普通文本按160字符加后缀截断。'],
  'sources': ['kuma-project/kuma-project-lab/src/main/java/com/kuma/cloud/lab/auditlog/AuditLogLearningDemo.java'],
  'doc': '/lab-guides/audit-log-labs.md',
  'mainClass': 'com.kuma.cloud.lab.auditlog.AuditLogLearningDemo',
  'mainArgs': 'content'},
 {'kind': 'learning',
  'id': 'audit-log-proxy',
  'group': '日志审计实验',
  'name': '审计代理边界与自调用',
  'description': '复用真实 audit-log starter 和 Spring AOP，逐步返回审计证据与检查结果，每次运行隔离。',
  'runtime': 'JDK 25 · Spring AOP · 无外部中间件',
  'commands': './gradlew.bat :kuma-project:kuma-project-lab:auditLogLab -PauditLesson=proxy',
  'topics': ['Spring AOP', '代理', 'self invocation', 'this调用'],
  'expected': ['自调用无新增记录；通过Spring代理调用相同方法新增一条审计。'],
  'sources': ['kuma-project/kuma-project-lab/src/main/java/com/kuma/cloud/lab/auditlog/AuditLogLearningDemo.java'],
  'doc': '/lab-guides/audit-log-labs.md',
  'mainClass': 'com.kuma.cloud.lab.auditlog.AuditLogLearningDemo',
  'mainArgs': 'proxy'},
 {'kind': 'learning',
  'id': 'audit-log-storage',
  'group': '日志审计实验',
  'name': '审计存储失败的业务语义',
  'description': '复用真实 audit-log starter 和 Spring AOP，逐步返回审计证据与检查结果，每次运行隔离。',
  'runtime': 'JDK 25 · Spring AOP · 无外部中间件',
  'commands': './gradlew.bat :kuma-project:kuma-project-lab:auditLogLab -PauditLesson=storage',
  'topics': ['AuditLogStore', '故障隔离', '审计丢失'],
  'expected': ['存储抛错时业务仍成功，但没有审计记录；不能以业务成功推断审计可靠。'],
  'sources': ['kuma-project/kuma-project-lab/src/main/java/com/kuma/cloud/lab/auditlog/AuditLogLearningDemo.java'],
  'doc': '/lab-guides/audit-log-labs.md',
  'mainClass': 'com.kuma.cloud.lab.auditlog.AuditLogLearningDemo',
  'mainArgs': 'storage'}])

for prefix, group, task, main, runtime, lessons in [
    ('mysql-advanced', 'MySQL 索引与事务进阶', 'mysqlAdvancedLab', 'MysqlAdvancedLearningDemo', 'JDK 25 · MySQL/InnoDB', [
        ('all', '完整索引与事务实验', ['EXPLAIN', 'MVCC', '行锁/间隙锁', '死锁', 'Spring事务']),
        ('indexes', '联合索引、覆盖与回表', ['EXPLAIN', '最左前缀', '覆盖索引', '回表']),
        ('isolation', 'RC/RR 与 MVCC', ['快照读', '当前读', '提交可见性']),
        ('locks', '行锁与间隙锁', ['FOR UPDATE', '1205超时', '回滚释放']),
        ('deadlock', '真实 InnoDB 死锁', ['交叉持锁', '1213牺牲者', '事务回滚']),
        ('propagation', 'Spring 事务传播与失效', ['REQUIRED', 'REQUIRES_NEW', 'NESTED', 'rollback-only', 'UnexpectedRollbackException', '自调用', 'rollbackFor']),
        ('optimistic', '丢失更新与版本冲突控制', ['双连接交错', 'version', '影响行数', '有限重试', '条件原子UPDATE']),
    ]),
    ('redis-advanced', 'Redis 缓存与分布式锁进阶', 'redisAdvancedLab', 'RedisAdvancedLearningDemo', 'JDK 25 · Redis · 缓存项需MySQL', [
        ('all', '完整缓存与锁实验', ['穿透/击穿/雪崩', '一致性', '租约锁', 'Lua']),
        ('cache', '空值缓存与热点互斥重建', ['真实MySQL回源', '6并发', '锁内二次检查']),
        ('avalanche', '集中到期与错峰TTL', ['真实过期', '首波失效', 'TTL分布']),
        ('consistency', '旧值回填与数据库缓存一致性', ['真实MySQL/Redis', '固定交错', '共用锁协调']),
        ('locks', '租约、误释放与续期', ['SET NX', 'token校验', 'Lua解锁', '手动续期']),
        ('lua', 'Lua并发库存扣减', ['原子检查扣减', '12请求', '5份库存']),
        ('fencing', '陈旧持有者与下游写入屏障', ['租约失效', 'fencing token', 'MySQL条件写', '重放拒绝', '序列回退边界']),
    ]),
    ('threadpool', 'Java 线程池与异步进阶', 'threadPoolLab', 'ThreadPoolLearningDemo', 'JDK 25 · 无需中间件', [
        ('all', '完整线程池与异步实验', ['队列', '拒绝', '异常', '取消', '饥饿', 'MDC']),
        ('queue', '有界队列与拒绝策略', ['AbortPolicy', 'CallerRunsPolicy', '反压']),
        ('exceptions', 'execute / submit 异常差异', ['UncaughtExceptionHandler', 'ExecutionException']),
        ('cancellation', '等待超时与任务取消', ['get(timeout)', 'cancel(true)', '协作中断']),
        ('starvation', '嵌套等待饥饿与异步组合', ['单线程池', 'CompletableFuture', '非阻塞组合']),
        ('context', 'ThreadLocal / MDC 传递与清理', ['线程复用残留', '提交时快照', 'finally恢复']),
        ('bulkhead', '慢依赖隔离与超时预算', ['共享池阻塞', 'bulkhead', '有界拒绝', 'orTimeout', '底层任务继续执行']),
    ]),
]:
    for lesson, name, topics in lessons:
        if prefix == 'threadpool':
            command = f'./gradlew.bat :kuma-project:kuma-project-lab:{task} -Pp0Lesson={lesson}'
        else:
            group_arg = 'mysql' if prefix.startswith('mysql') else 'redis'
            command = f'powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-p0-labs-wsl.ps1 -Group {group_arg} -Lesson {lesson}'
        add(f'{prefix}-{lesson}', group, name, '独立数据与小型线程池，返回真实逐步证据和断言；手动运行并自动清理。',
            runtime, command, topics, ['所选实验checks全部为true；详见教程中的实际值与边界。'],
            [f'{java}/p0/{main}.java'], 'docs/p0-advanced-labs.md', f'com.kuma.cloud.lab.p0.{main}')
        cards[-1]['mainArgs'] = lesson

for lesson, name, description, topics, expected in [
    ('all', 'Java 基础完整实验', '串联语法、volatile、对象、Mark Word、类加载、继承、抽象类、对象初始化和值传递。',
     ['Java 基础', '语法', 'volatile', '对象', 'Mark Word', '类加载', '继承', '抽象类', '对象初始化', '值传递'], ['九组实验全部通过，最后输出 Java basics: all passed。']),
    ('syntax', 'Java 语法与基础类型', '运行数值、控制流、字符串、装箱、泛型、集合、Lambda 和异常语法。',
     ['自增', '类型转换', '溢出', '移位', '短路', 'switch', 'String', '装箱/拆箱', '泛型', 'Stream', 'try-with-resources'],
     ['数值和控制流结果正确；null 拆箱抛预期异常。', '资源逆序关闭，主异常保留两个 suppressed 异常。']),
    ('volatile', 'volatile 基础：可见性与非原子性', '真实线程验证安全发布；固定读写交错展示复合更新丢失，与 AtomicInteger 对照。',
     ['volatile', '可见性', 'happens-before', '安全发布', '非原子性', 'AtomicInteger'],
     ['读取到 payload=42；两线程各100次更新后 volatile=100，AtomicInteger=200。', '显式拆分读写并用屏障固定交错，不将自然调度概率作为断言。']),
    ('objects', '对象基础：引用、值传递与多态', '观察对象引用、字段隐藏、动态分派、相等契约和浅不可变的差异。',
     ['对象', '值传递', '继承', '多态', 'equals', 'hashCode', 'record', '浅拷贝'],
     ['对象变更可见，形参重新赋值不影响调用方；字段取父类、方法执行子类实现。', 'HashSet 去重；共享集合发生变化，复制集合保持原内容。']),
    ('markword', 'Mark Word 基础：同一对象头变化', '使用 JOL 观察同一对象在计算 identity hash 和 synchronized 前后的真实布局。',
     ['Mark Word', 'markword', '对象头', 'JOL', 'identityHashCode', 'synchronized', '字段对齐'],
     ['输出 fresh / after hash / held / released 四次布局和带字段对象布局。', '持锁检查与 identity hash 稳定性通过；头部尺寸及位模式不固定断言，JOL 警告见教程。']),
    ('classloading', '类加载基础：初始化与类身份', '验证仅加载、常量内联、初始化顺序、双亲委派和不同加载器的同名类。',
     ['类加载', 'ClassLoader', '双亲委派', 'static', '初始化顺序', 'Class.forName', '类身份'],
     ['仅加载和编译期常量不触发初始化；父类静态初始化先执行且仅一次。', '两次实例构造执行完整实例顺序；不同 defining loader 的同名类不可赋值。']),
    ('inheritance', 'Java 继承：重写、重载与多态', '对照声明类型和运行时类型，观察字段隐藏、static隐藏、方法分派、super和强转。',
     ['继承', 'override', 'overload', '重写', '重载', 'super', 'instanceof', 'static'],
     ['父类引用调用子类重写方法，字段及static方法按声明类型选择。', '重载签名按编译期类型选择；非法向下转型抛出预期 ClassCastException。']),
    ('abstract', '抽象类与接口：模板方法与具体子类', '通过带状态和构造器的抽象类、接口默认方法、final模板和匿名子类运行具体实现。',
     ['抽象类', 'abstract', '接口', 'default', 'final', '模板方法', '匿名类'],
     ['Rectangle面积12，模板报告name=rectangle, area=12；匿名具体子类面积7。', '反射确认抽象修饰符；不能直接实例化等编译期约束见教程反例。']),
    ('initialization', '对象初始化：字段、代码块与构造链', '完整记录父子static、字段初始化、实例块、super/this构造链，并展示构造器动态分派风险。',
     ['对象初始化', '构造器', '初始化块', '默认值', 'super', 'this', '动态分派'],
     ['父类构造器调用子类重写方法时，子类字段为默认值0，完成构造后为20。', '第二次构造不执行static；this链不重复执行字段和实例块。']),
    ('passing', 'Java 值传递：基本类型、引用与容器', '逐项对照基本类型、对象、String、数组、List和swap，区别共享对象修改与形参重新赋值。',
     ['值传递', '引用', '形参', 'String', '数组', '集合', 'swap'],
     ['基本类型仍为1；对象和数组元素变为2；String仍为kuma；集合增加second。', '交换形参不能交换调用方变量；返回新值后可由调用方显式接收。']),
]:
    add(f'java-basics-{lesson}', 'Java 语法与对象基础', name, description, 'JDK 25 · Gradle · 无需中间件',
        f'./gradlew.bat :kuma-project:kuma-project-lab:javaBasicsLab -PjavaBasicsLesson={lesson}',
        topics, expected, [f'{java}/basics/{source}.java' for source in
                          ['JavaBasicsLearningDemo', 'SyntaxLesson', 'VolatileLesson', 'ObjectLesson', 'MarkWordLesson', 'ClassLoadingLesson', 'ObjectSemanticsLessons']],
        'docs/java-basics-labs.md', 'com.kuma.cloud.lab.basics.JavaBasicsLearningDemo')
    cards[-1]['mainArgs'] = lesson

for lesson, name, topics, expected in [
    ('all', 'Java 锁与并发完整实验', ['锁', '并发', 'synchronized', 'CAS', '线程协作', '异步'], ['十二组实验通过，输出 Java concurrency: all passed。']),
    ('locks', '各种锁完整实验', ['锁', 'synchronized', 'ReentrantLock', '读写锁', 'StampedLock', 'Semaphore', '死锁'], ['七组锁实验通过；等待有界且所有线程可回收。']),
    ('concurrency', 'Java 并发完整实验', ['并发', '原子更新', '线程协作', '阻塞队列', '异步', 'ThreadLocal'], ['五组并发实验通过；所有工作线程在退出前回收。']),
    ('monitor', 'synchronized：互斥、重入与异常释放', ['锁', 'synchronized', 'monitor', '可重入', '互斥'], ['两线程计数2000；异常退出后另一个线程能获得同一monitor。']),
    ('reentrant', 'ReentrantLock：超时、中断与 Condition', ['锁', 'ReentrantLock', '公平锁', 'tryLock', 'lockInterruptibly', 'Condition'], ['重入计数2；timed tryLock失败；等待者响应中断；Condition通知后观察到ready=true。']),
    ('readwrite', '读写锁：读共享、写排他与降级', ['锁', 'ReentrantReadWriteLock', '读写锁', '共享锁', '排他锁', '锁降级'], ['另一个读者可进入，写者超时获取失败；写锁降级后保留一个读锁。']),
    ('stamped', 'StampedLock：乐观读与校验回退', ['锁', 'StampedLock', '乐观读', 'validate', '读锁'], ['读取旧值1后发生真实线程写入2，旧stamp校验失败，读锁回退读到2。']),
    ('semaphore', 'Semaphore：许可与并发资源上限', ['锁', 'Semaphore', '信号量', '共享许可', '限流'], ['两个任务占满许可，第三个任务获取失败；任务结束后全部归还。']),
    ('waitnotify', 'wait/notify：条件等待与唤醒', ['锁', 'wait', 'notifyAll', 'monitor', '条件循环'], ['等待者释放monitor后写者更新42并通知；未持锁notifyAll抛出预期异常。']),
    ('deadlock', '交叉持锁：循环等待与中断恢复', ['锁', '死锁', '循环等待', 'lockInterruptibly', '锁顺序'], ['两个任务分别持一把锁再等待对方；取消后至少一个被中断，两个锁最终均释放。']),
    ('atomic', '并发更新：CAS、ABA 与单键原子计算', ['并发', 'CAS', 'ABA', 'AtomicInteger', 'AtomicStampedReference', 'ConcurrentHashMap'], ['固定交错普通计数100，原子计数和compute均为200；普通CAS接受ABA，旧版本戳CAS被拒绝。']),
    ('coordination', '线程协作：Latch、Barrier 与 Phaser', ['并发', 'CountDownLatch', 'CyclicBarrier', 'Phaser', '线程协作'], ['两个工作线程完成四次工作；Barrier复用两轮；Phaser注销后终止。']),
    ('queue', '阻塞队列：生产消费与背压', ['并发', 'ArrayBlockingQueue', 'BlockingQueue', '生产者消费者', '背压', 'FIFO'], ['容量2的队列拒绝第三项；真实并发消费收到1～5，终止标记结束消费。']),
    ('future', '异步任务：组合、异常与取消', ['并发', 'Future', 'CompletableFuture', 'thenCombine', '异常', '取消', '中断'], ['组合结果42；异常降级-1且原始cause保留；取消的Future抛出CancellationException，任务响应中断。']),
    ('threadlocal', 'ThreadLocal：线程池复用与清理', ['并发', 'ThreadLocal', '线程复用', '上下文', 'remove'], ['主线程上下文不自动传播；工作线程残留task-one；finally.remove后下一任务为null。']),
]:
    add(f'java-concurrent-{lesson}', 'Java 锁与并发实验', name,
        '真实线程、固定交错、有限等待与显式断言；每项可单独运行，无需服务或中间件。',
        'JDK 25 · Gradle · 无需中间件',
        f'./gradlew.bat :kuma-project:kuma-project-lab:javaConcurrencyLab -PconcurrencyLesson={lesson}',
        topics, expected, [f'{java}/concurrent/{source}.java' for source in
                          ['ConcurrencyLearningDemo', 'LockExperiments', 'ConcurrentExperiments']],
        'docs/java-concurrency-labs.md', 'com.kuma.cloud.lab.concurrent.ConcurrencyLearningDemo')
    cards[-1]['mainArgs'] = lesson

for lesson, group, name, topics, expected in [
    ('all', 'Java 运行时与资源', '类加载、内存、CPU 与线程池完整实验', ['类加载', '内存', 'CPU', '线程池'], ['十二项实验通过，输出 Java runtime: all passed；连接池使用独立入口。']),
    ('classloading', 'Java 类加载进阶', '类加载进阶完整实验', ['类加载', '初始化失败', 'ClassLoader', 'TCCL'], ['初始化失败、加载器隔离与上下文资源三项通过。']),
    ('loader-failure', 'Java 类加载进阶', '类加载：初始化失败与错误状态', ['类加载', 'static', 'ExceptionInInitializerError', 'NoClassDefFoundError'], ['仅加载不执行static；首次初始化抛ExceptionInInitializerError，后续抛NoClassDefFoundError，初始化只尝试一次。']),
    ('loader-identity', 'Java 类加载进阶', '类加载：定向隔离与默认委派', ['类加载', 'ClassLoader', '双亲委派', '类身份', 'ClassNotFoundException'], ['同一加载器缓存Class；不同加载器的同名类不可赋值；String仍委派；缺失类抛预期异常。']),
    ('loader-context', 'Java 类加载进阶', '类加载：TCCL 与资源查找', ['类加载', 'TCCL', 'URLClassLoader', '资源', '上下文加载器'], ['显式使用TCCL读取临时隔离资源，finally恢复原加载器并删除实验临时文件。']),
    ('memory', 'Java 内存与生命周期', '内存与生命周期完整实验', ['内存', '堆', '直接内存', '引用', 'Arena'], ['内存区域、引用持有、原生段生命周期三项通过；不依赖固定GC时机。']),
    ('memory-regions', 'Java 内存与生命周期', '内存：堆、非堆与直接缓冲区', ['内存', 'heap', 'non-heap', 'ByteBuffer', 'GC', 'BufferPoolMXBean'], ['4MiB堆数组和2MiB直接缓冲区读写正确；输出堆、非堆、缓冲池及GC指标。']),
    ('memory-retention', 'Java 内存与生命周期', '内存：强引用持有与容器清理', ['内存', '强引用', '弱引用', 'WeakReference', 'GC', '内存泄漏'], ['8块256KiB数组被容器持有，clear后逻辑持有解除；弱引用回收仅记录观测值。']),
    ('memory-lifetime', 'Java 内存与生命周期', '内存：Arena 生命周期与访问约束', ['内存', 'Arena', 'MemorySegment', 'FFM', '越界', '线程约束'], ['原生段读到42，越界和跨线程访问被拒绝；close后访问抛IllegalStateException。']),
    ('cpu', 'CPU 与线程调度', 'CPU 与线程调度完整实验', ['CPU', 'CPU time', 'wall time', '并行', '线程状态'], ['计时、两分片计算和线程状态三项通过；耗时比值及加速比不作为断言。']),
    ('cpu-time', 'CPU 与线程调度', 'CPU：运行时间与线程 CPU 时间', ['CPU', 'ThreadMXBean', 'CPU time', 'wall time', '等待'], ['输出有限计算与60ms等待的wall/CPU纳秒数；不支持CPU计时则标记-1。']),
    ('cpu-parallel', 'CPU 与线程调度', 'CPU：有界并行计算与结果合并', ['CPU', '并行', '分片', '线程池', 'JIT'], ['两个各100万项分片合并结果与200万项串行计算相同；打印耗时，不保证并行更快。']),
    ('cpu-states', 'CPU 与线程调度', 'CPU：monitor 阻塞与带超时等待', ['CPU', '线程调度', 'BLOCKED', 'TIMED_WAITING', 'ThreadInfo'], ['观察真实BLOCKED状态及锁持有者ID；观察Latch的TIMED_WAITING；释放条件后任务均完成。']),
    ('threadpool', 'Java 线程池容量与生命周期', '线程池容量与生命周期完整实验', ['并发池', '线程池', 'ThreadPoolExecutor', 'shutdown', '虚拟线程'], ['扩容、关闭及虚拟线程下游容量三项通过。']),
    ('pool-growth', 'Java 线程池容量与生命周期', '线程池：核心、队列、扩容与拒绝', ['并发池', '线程池', 'corePoolSize', 'maximumPoolSize', '有界队列', '拒绝'], ['核心1/最大2/队列1：先排队、队列满再扩容、第四项拒绝；已接受任务全部完成。']),
    ('pool-shutdown', 'Java 线程池容量与生命周期', '线程池：有序关闭与立即关闭', ['并发池', '线程池', 'shutdown', 'shutdownNow', 'Future', '取消'], ['shutdown完成已接受任务；shutdownNow中断运行任务并返回排队Future，实验显式取消该Future。']),
    ('pool-virtual', 'Java 线程池容量与生命周期', '虚拟线程：任务并发与下游容量', ['并发池', '线程池', '虚拟线程', 'Semaphore', '下游容量'], ['32个虚拟线程任务在3个许可下运行，下游占用峰值3，结束后许可全部归还。']),
]:
    add(f'java-runtime-{lesson}', group, name,
        '独立JVM、有限资源与显式断言；与已有Java基础、JMM及线程池进阶实验配合阅读。',
        'JDK 25 · Gradle · 无需中间件',
        f'./gradlew.bat :kuma-project:kuma-project-lab:javaRuntimeLab -PruntimeLesson={lesson}', topics, expected,
        [f'{java}/runtime/{source}.java' for source in
         ['RuntimeLearningDemo', 'LoaderExperiments', 'MemoryExperiments', 'CpuExperiments', 'PoolExperiments']],
        'docs/runtime-resource-labs.md', 'com.kuma.cloud.lab.runtime.RuntimeLearningDemo')
    cards[-1]['mainArgs'] = lesson

jdbc_script = 'powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1'
for lesson, name, topics, expected in [
    ('all', 'JDBC 连接池完整实验', ['连接池', 'HikariCP', 'JDBC', 'PostgreSQL', '事务', '线程池'], ['四项真实JDBC实验通过，输出 Java connection pool: all passed。']),
    ('reuse', '连接池：逻辑关闭与物理连接复用', ['连接池', 'HikariCP', 'JDBC', 'close', 'unwrap', '连接复用'], ['两次借用逻辑句柄不同、解包后的PostgreSQL物理连接相同，物理连接仍打开。']),
    ('timeout', '连接池：耗尽、获取超时与恢复', ['连接池', 'connectionTimeout', '连接耗尽', 'SQLTransientConnectionException'], ['唯一连接未归还时，另一个线程获取超时；归还后再次借用并执行SELECT 42成功。']),
    ('transaction', '连接池：事务回滚与连接状态复位', ['连接池', '事务', 'rollback', 'commit', 'autoCommit', '隔离级别'], ['未提交插入在归还时回滚；autoCommit与隔离级别复位；显式提交一行、回滚一行后只剩一行。']),
    ('capacity', '线程池与连接池：容量差异与等待', ['连接池', '线程池', '并发池', '等待连接', '背压', '容量'], ['4个工作线程竞争2个真实连接，观测到等待者；最终4行提交，连接占用峰值2。']),
]:
    add(f'java-connection-pool-{lesson}', 'Java JDBC 连接池', name,
        '使用现有WSL PostgreSQL的kuma_lab专用库，每次独立schema，保留数据供查询；凭据从Pod运行时读取。',
        'JDK 25 · Gradle · WSL PostgreSQL · HikariCP',
        jdbc_script + f' -Group connectionpool -Lesson {lesson}', topics, expected,
        [f'{java}/runtime/ConnectionPoolLearningDemo.java', f'{java}/runtime/RuntimeLearningDemo.java',
         f'{java}/database/LabDatabase.java', 'scripts/run-database-labs-wsl.ps1', 'scripts/prepare-lab-postgresql.sh'],
        'docs/runtime-resource-labs.md', 'com.kuma.cloud.lab.runtime.ConnectionPoolLearningDemo')
    cards[-1]['mainArgs'] = lesson

for lesson, group, name, topics, expected in [
    ('all', '数据库与事务完整实验', '数据库操作与事务完整实验', ['数据库', 'SQL', 'JDBC', '事务', 'Spring', 'PostgreSQL'], ['五项数据库操作和七项事务实验全部通过，输出 Database learning: all passed。']),
    ('operations', '数据库操作基础', '数据库操作完整实验', ['数据库', 'SQL', 'CRUD', '参数绑定', '批处理', '查询', '约束'], ['CRUD、绑定、批处理、查询与约束五项通过。']),
    ('crud', '数据库操作基础', 'CRUD：主键生成、增查改删与行数', ['数据库', 'SQL', 'CRUD', 'INSERT', 'SELECT', 'UPDATE', 'DELETE', '生成主键'], ['生成主键用于读写；修改后价格15；首次删除1行、重复删除0行，最终表为空。']),
    ('binding', '数据库操作基础', '参数绑定：引号、NULL 与金额', ['数据库', 'SQL', 'PreparedStatement', '参数绑定', 'SQL注入', 'NULL', 'BigDecimal'], ['注入形状字符串仅按字面值匹配；含引号文本、NULL和DECIMAL金额1.25正确读回。']),
    ('batch', '数据库操作基础', '批处理：成功提交与整批失败回滚', ['数据库', 'SQL', '批处理', 'executeBatch', 'BatchUpdateException', '事务', 'rollback'], ['成功批次提交3行；后续批次发生CHECK约束错误，回滚后不留下已执行部分，最终仍为3行。']),
    ('queries', '数据库操作基础', 'SQL 查询：关联、聚合、窗口与分页', ['数据库', 'SQL', 'JOIN', 'GROUP BY', 'HAVING', '窗口函数', 'ROW_NUMBER', '分页'], ['LEFT JOIN保留未售商品；food收入15；价格排名book/rice/apple；固定数据上两种分页结果一致。']),
    ('constraints', '数据库操作基础', '数据约束：主键、唯一、非空、检查与外键', ['数据库', 'SQL', '主键', '唯一约束', 'NOT NULL', 'CHECK', '外键', 'SQLState'], ['五种约束均返回23类SQLState；失败语句不改变原有两行账户和余额。']),
    ('transactions', 'JDBC 与 Spring 事务', '事务完整实验', ['数据库', '事务', 'commit', 'rollback', '保存点', '隔离级别', '乐观锁', 'Spring'], ['边界、转账、保存点、隔离、乐观更新、锁等待、Spring传播七项通过。']),
    ('boundaries', 'JDBC 与 Spring 事务', '事务边界：自动提交、commit 与 rollback', ['数据库', '事务', 'autoCommit', 'commit', 'rollback', '可见性'], ['自动提交后95；未提交时自身85、观察者95；commit后85；后续rollback仍为85。']),
    ('transfer', 'JDBC 与 Spring 事务', '转账事务：扣款故障与原子回滚', ['数据库', '事务', '转账', '原子性', '故障注入', '条件更新', '余额不足'], ['成功转账后70/80，扣款后注入主键冲突触发回滚，余额仍70/80、总额150、审计仅1行；余额不足不扣款。']),
    ('savepoint', 'JDBC 与 Spring 事务', '事务保存点：局部回滚与外层提交', ['数据库', '事务', 'Savepoint', '保存点', '局部回滚'], ['回滚到保存点撤销Bob的999增量、保留Alice扣款，继续提交后余额90/60。']),
    ('isolation', 'JDBC 与 Spring 事务', '事务隔离：双连接 RC 与 RR 对照', ['数据库', '事务', '隔离级别', 'READ_COMMITTED', 'REPEATABLE_READ', '脏读', '不可重复读'], ['两种级别均不读未提交值；写者提交后RC读110、RR保持100；开启新事务后均读110。']),
    ('optimistic', 'JDBC 与 Spring 事务', '并发更新：丢失更新、版本冲突与重试', ['数据库', '事务', '丢失更新', '乐观锁', 'version', '影响行数', '重试'], ['旧值覆盖余额80；版本条件拒绝旧版本更新，刷新版本后仅重试一次，最终余额70、版本2。']),
    ('locking', 'JDBC 与 Spring 事务', '行锁：FOR UPDATE、等待超时与恢复', ['数据库', '事务', '行锁', 'SELECT FOR UPDATE', '锁等待', '超时'], ['持行锁时另一线程更新超时，PostgreSQL返回SQLState 55P03；提交释放锁后可更新到101。']),
    ('spring', 'JDBC 与 Spring 事务', 'Spring 事务：独立提交、嵌套回滚与回滚标记', ['数据库', '事务', 'Spring', 'TransactionTemplate', 'REQUIRED', 'REQUIRES_NEW', 'NESTED', 'rollback-only', 'UnexpectedRollbackException'], ['REQUIRES_NEW审计提交不随外层回滚；NESTED局部回滚后外层提交90/60；REQUIRED内层异常即使捕获仍使整体回滚。']),
]:
    add(f'database-{lesson}', group, name,
        '默认使用WSL真实PostgreSQL的kuma_lab专用库，独立schema保留数据；打印SQL、结果及显式检查。',
        'JDK 25 · Gradle · WSL PostgreSQL · Spring事务',
        jdbc_script + f' -Group database -Lesson {lesson}', topics, expected,
        [f'{java}/database/{source}.java' for source in
         ['DatabaseLearningDemo', 'DatabaseSupport', 'LabDatabase', 'DatabaseOperations', 'TransactionExperiments', 'SpringTransactionExperiments']]
        + ['scripts/run-database-labs-wsl.ps1', 'scripts/prepare-lab-postgresql.sh'],
        'docs/database-transaction-labs.md', 'com.kuma.cloud.lab.database.DatabaseLearningDemo')
    cards[-1]['mainArgs'] = lesson

for lesson, name, topics, expected in [
    ('all', 'PostgreSQL MVCC、行锁与表锁完整实验', ['数据库', 'PostgreSQL', 'MVCC', '行锁', '表锁', '阻塞链', '可串行化'], ['六项实验在同一专属schema内通过，observations表保留快照与锁观测；输出 PostgreSQL concurrency: all passed。']),
    ('mvcc', 'MVCC：旧版本、新版本与快照', ['数据库', 'PostgreSQL', 'MVCC', 'xmin', 'ctid', '快照', 'REPEATABLE READ'], ['未提交更新期间普通读仍为100；提交后RR读旧版本100，新语句读110；记录xmin/ctid/快照，旧事务结束后读110。']),
    ('phantom', 'MVCC：RC/RR 与新增行可见性', ['数据库', 'PostgreSQL', 'MVCC', '幻读', 'RC', 'RR', '快照'], ['并发连接插入提交后，RC条件查询行数2→3，PostgreSQL RR保持2；新事务都读3行。']),
    ('row-locks', '行锁：NOWAIT、SKIP LOCKED 与不同记录', ['数据库', 'PostgreSQL', '行锁', 'FOR UPDATE', 'NOWAIT', 'SKIP LOCKED'], ['锁住行1后普通读不受该行锁阻塞；NOWAIT抛55P03；SKIP LOCKED仅得行2，更新行2成功。']),
    ('table-locks', '表锁：SHARE 与 ACCESS EXCLUSIVE', ['数据库', 'PostgreSQL', '表锁', 'LOCK TABLE', 'ShareLock', 'AccessExclusiveLock'], ['SHARE允许普通SELECT但阻止写；ACCESS EXCLUSIVE连普通SELECT也阻止；超时55P03，释放后恢复。']),
    ('lock-monitor', '锁观测：等待者、持有者与阻塞链', ['数据库', 'PostgreSQL', '行锁', 'pg_locks', 'pg_stat_activity', 'pg_blocking_pids', '阻塞链'], ['实际等待时记录后端PID、Lock等待事件和未授予锁；提交持有者后写入完成到101且阻塞链为空。']),
    ('serializable', 'MVCC：写偏差与可串行化冲突', ['数据库', 'PostgreSQL', 'MVCC', '写偏差', 'SERIALIZABLE', 'SSI', '40001'], ['两个事务读取均有两人值班：RR允许分别下班导致0；SERIALIZABLE拒绝一个事务并返回40001，重新检查后保留1人。']),
]:
    add(f'postgres-{lesson}', 'PostgreSQL MVCC 与锁', name,
        '使用WSL PostgreSQL的kuma_lab库；本次运行共用独立schema，保留业务表和observations证据表，不清空历史实验。',
        'JDK 25 · Gradle · WSL PostgreSQL', jdbc_script + f' -Group postgres -Lesson {lesson}', topics, expected,
        [f'{java}/database/PostgresConcurrencyLearningDemo.java', f'{java}/database/DatabaseSupport.java',
         f'{java}/database/LabDatabase.java', 'scripts/run-database-labs-wsl.ps1'],
        'docs/postgresql-mvcc-lock-labs.md', 'com.kuma.cloud.lab.database.PostgresConcurrencyLearningDemo')
    cards[-1]['mainArgs'] = lesson

guides = ('memory-labs.md', 'network-labs.md', 'jdk-version-labs.md', 'observability-labs.md', 'monitoring-middleware-labs.md', 'elasticsearch-labs.md', 'socket-session-labs.md', 'audit-log-labs.md', 'p0-advanced-labs.md', 'java-basics-labs.md', 'java-concurrency-labs.md', 'runtime-resource-labs.md', 'database-transaction-labs.md', 'postgresql-mvcc-lock-labs.md')
for name in guides:
    shutil.copyfile(lab / 'docs' / name, docs / name)
(renderer / 'lab-learning.json').write_text(json.dumps(cards, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
print(f'Generated {len(cards)} standalone lessons and {len(guides)} guides.')
