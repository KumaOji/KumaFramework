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


add('lock', 'Java 并发与内存', '并发与锁学习', '可见性、原子性、CAS、锁与任务完成通知。', 'JDK 25 · 本机',
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
     ['重复事件仅一份模拟业务效果；重试首次503、第二次202。', '本例为自定义教学签名协议，事件表仅在进程内保存。']),
    ('socket', 'TCP 分帧与 UDP 通信', 'SocketLesson', ['长度前缀', '半关闭', 'EOF', '读取超时', 'UDP 数据报'],
     ['三个 TCP 帧内容正确；非法帧被拒绝；超时后连接仍可用。', '两个 UDP 数据报完整回显；回环成功不代表公网可靠性。']),
]:
    add(identifier, '网络通信学习', name, '使用回环地址和临时端口，无需 Lab 服务或外部系统。', 'JDK 25 · 本机',
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
    add(identifier, 'OTel 与 SkyWalking 学习', name, description, runtime, command, topics, expected,
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

docs.mkdir(exist_ok=True)
guides = ('memory-labs.md', 'network-labs.md', 'jdk-version-labs.md', 'observability-labs.md', 'monitoring-middleware-labs.md')
for name in guides:
    shutil.copyfile(lab / 'docs' / name, docs / name)
(renderer / 'lab-learning.json').write_text(json.dumps(cards, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
print(f'Generated {len(cards)} standalone lessons and {len(guides)} guides.')
