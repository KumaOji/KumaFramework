package com.kuma.cloud.lab.kafka.service;

import com.kuma.cloud.lab.kafka.config.KafkaLabProperties;
import com.kuma.cloud.lab.kafka.domain.dto.KafkaScenarioDTO;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Utils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/** Real broker experiments. Each run owns a new topic and groups, never the normal listener's group. */
@Service
public class KafkaScenarioService {
    private static final Duration API_TIMEOUT = Duration.ofSeconds(8);
    private final KafkaLabProperties properties;
    private final ExecutorService workers = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore slots = new Semaphore(2);
    private final Map<String, Experiment> experiments = new LinkedHashMap<>();

    public KafkaScenarioService(KafkaLabProperties properties) { this.properties = properties; }

    public synchronized Map<String, Object> start(KafkaScenarioDTO request) {
        requireEnabled();
        if (request == null || request.getPartitions() < 2 || request.getPartitions() > 6
                || request.getMessageCount() < 4 || request.getMessageCount() > 20
                || request.getReplicationFactor() < 1 || request.getReplicationFactor() > 3
                || request.getMessage() == null || request.getMessage().length() > 1024)
            throw new IllegalArgumentException("分区 2–6、消息 4–20、副本 1–3，消息长度最多 1024");
        if (!slots.tryAcquire()) throw new IllegalStateException("已有两个 Kafka 实验运行中，请等待完成");
        while (experiments.size() >= 100) {
            var oldest = experiments.values().stream().filter(e -> !e.running()).findFirst();
            if (oldest.isEmpty()) { slots.release(); throw new IllegalStateException("实验记录已满"); }
            experiments.remove(oldest.get().id);
        }
        var experiment = new Experiment(UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        experiments.put(experiment.id, experiment);
        // Copy the DTO before scheduling: the experiment's parameters cannot change during a run.
        int partitions=request.getPartitions(), count=request.getMessageCount();
        short replicas=request.getReplicationFactor(); String message=request.getMessage();
        boolean rebalance=request.isDemonstrateRebalance();
        workers.submit(() -> {
            try { run(experiment, partitions, count, replicas, message, rebalance); }
            catch (Exception error) {
                if (error instanceof InterruptedException) Thread.currentThread().interrupt();
                Throwable cause=error; while(cause.getCause()!=null)cause=cause.getCause();
                String detail=cause.getMessage();
                if(detail==null||detail.isBlank())detail="操作未在期限内完成，请检查 bootstrap 和 broker advertised.listeners 从当前机器是否可达";
                experiment.fail(cause.getClass().getSimpleName()+": "+detail);
            } finally { experiment.finish(); slots.release(); }
        });
        return experiment.snapshot();
    }

    public synchronized Map<String, Object> get(String id) { return experiment(id).snapshot(); }
    public synchronized List<Map<String, Object>> latest() {
        return experiments.values().stream().toList().reversed().stream().limit(20).map(Experiment::snapshot).toList();
    }
    public Map<String, Object> cleanup(String id) throws Exception {
        requireEnabled(); Experiment experiment;
        synchronized(this) { experiment=experiment(id); }
        synchronized(experiment) {
            if (experiment.running()) throw new IllegalArgumentException("实验运行中，不能清理 topic");
            if (experiment.cleaned) return experiment.snapshot();
            try (Admin admin=admin()) {
                try { admin.deleteTopics(List.of(experiment.topic)).all().get(8,TimeUnit.SECONDS); }
                catch(ExecutionException error) {
                    if (!(error.getCause() instanceof org.apache.kafka.common.errors.UnknownTopicOrPartitionException)) throw error;
                }
                for(String group:List.of(experiment.group,experiment.otherGroup,experiment.balanceGroup)) {
                    try { admin.deleteConsumerGroups(List.of(group)).all().get(8,TimeUnit.SECONDS); }
                    catch(ExecutionException error) {
                        if (!(error.getCause() instanceof org.apache.kafka.common.errors.GroupIdNotFoundException)) throw error;
                    }
                }
            }
            experiment.cleaned=true;
            experiment.step("CLEANUP","deleteTopics / deleteConsumerGroups","仅删除本次实验创建的 topic 和消费组",Map.of("topic",experiment.topic));
            return experiment.snapshot();
        }
    }
    private Experiment experiment(String id) {
        var result=experiments.get(id);
        if(result==null)throw new IllegalArgumentException("未知实验 ID："+id);
        return result;
    }
    private void requireEnabled() {
        if(!properties.enabled())throw new IllegalStateException("请先配置 kuma.lab.kafka.enabled=true");
    }
    private Map<String,Object> baseConfig() {
        return new HashMap<>(Map.of("bootstrap.servers",properties.bootstrapServers(),
                "request.timeout.ms",5000,"default.api.timeout.ms",8000));
    }
    private Admin admin() { return Admin.create(baseConfig()); }
    private KafkaProducer<String,String> producer() {
        var config=baseConfig();config.put("key.serializer",StringSerializer.class);config.put("value.serializer",StringSerializer.class);
        config.put("acks","all");config.put("enable.idempotence",true);config.put("max.block.ms",8000);
        config.put("delivery.timeout.ms",12000);config.put("linger.ms",0);
        return new KafkaProducer<>(config);
    }
    private KafkaConsumer<String,String> consumer(String group) {
        var config=baseConfig();config.put("key.deserializer",StringDeserializer.class);config.put("value.deserializer",StringDeserializer.class);
        config.put("group.id",group);config.put("enable.auto.commit",false);config.put("auto.offset.reset","earliest");
        config.put("allow.auto.create.topics",false);config.put("max.poll.records",20);
        return new KafkaConsumer<>(config);
    }

    public Map<String,Object> cluster() throws Exception {
        requireEnabled();try(Admin admin=admin()) {
            var result=admin.describeCluster();
            return Map.of("bootstrapServers",properties.bootstrapServers(),"clusterId",result.clusterId().get(8,TimeUnit.SECONDS),
                    "controller",node(result.controller().get(8,TimeUnit.SECONDS)),
                    "brokers",result.nodes().get(8,TimeUnit.SECONDS).stream().map(KafkaScenarioService::node).toList(),
                    "topics",admin.listTopics().names().get(8,TimeUnit.SECONDS).stream().sorted().toList());
        }
    }
    public Map<String,Object> topic(String topic, String group) throws Exception {
        requireEnabled();if(topic==null||!topic.matches("[A-Za-z0-9._-]{1,249}")||Set.of(".","..").contains(topic))
            throw new IllegalArgumentException("topic 名称不合法");
        try(Admin admin=admin()) {
            var description=admin.describeTopics(List.of(topic)).allTopicNames().get(8,TimeUnit.SECONDS).get(topic);
            var partitions=description.partitions().stream().map(p->new TopicPartition(topic,p.partition())).toList();
            var earliest=offsets(admin,partitions,OffsetSpec.earliest());var end=offsets(admin,partitions,OffsetSpec.latest());
            Map<TopicPartition,OffsetAndMetadata> committed=group==null||group.isBlank()?Map.of():
                    admin.listConsumerGroupOffsets(group).partitionsToOffsetAndMetadata().get(8,TimeUnit.SECONDS);
            List<Map<String,Object>> rows=new ArrayList<>();
            for(var p:description.partitions()) {
                var tp=new TopicPartition(topic,p.partition());var offset=committed.get(tp);
                var row=new LinkedHashMap<String,Object>();row.put("partition",p.partition());row.put("leader",node(p.leader()));
                row.put("replicas",p.replicas().stream().map(Node::id).toList());row.put("isr",p.isr().stream().map(Node::id).toList());
                row.put("beginningOffset",earliest.get(tp));row.put("endOffset",end.get(tp));
                row.put("committedOffset",offset==null?null:offset.offset());
                row.put("lag",offset==null?null:Math.max(0,end.get(tp)-offset.offset()));rows.add(row);
            }
            var resource=new ConfigResource(ConfigResource.Type.TOPIC,topic);
            var configs=admin.describeConfigs(List.of(resource)).all().get(8,TimeUnit.SECONDS).get(resource);
            var selected=new LinkedHashMap<String,Object>();
            for(String key:List.of("cleanup.policy","retention.ms","retention.bytes","min.insync.replicas","max.message.bytes")) {
                var entry=configs.get(key);if(entry!=null)selected.put(key,entry.isSensitive()?"[hidden]":entry.value());
            }
            return Map.of("topic",topic,"groupId",group==null?"":group,"partitions",rows,"configs",selected,
                    "note","endOffset 是下一条写入位置；committedOffset 是下一条待处理位置。无提交时 lag 为 null。");
        }
    }
    private static Map<TopicPartition,Long> offsets(Admin admin,List<TopicPartition> partitions,OffsetSpec spec) throws Exception {
        Map<TopicPartition,OffsetSpec> query=new LinkedHashMap<>();partitions.forEach(p->query.put(p,spec));
        Map<TopicPartition,Long> result=new LinkedHashMap<>();
        admin.listOffsets(query).all().get(8,TimeUnit.SECONDS).forEach((p,o)->result.put(p,o.offset()));return result;
    }
    private static Map<String,Object> node(Node node) {
        return node==null?Map.of():Map.of("id",node.id(),"host",node.host(),"port",node.port(),"rack",node.rack()==null?"":node.rack());
    }

    private void run(Experiment e,int partitionCount,int count,short replicas,String message,boolean rebalance) throws Exception {
        e.step("DISCOVERY_START","describeCluster","准备连接集群；TCP 端口可达不等于返回的 broker 地址可达",Map.of("bootstrapServers",properties.bootstrapServers()));
        e.step("DISCOVERY","describeCluster","bootstrap.servers 是发现入口，实际 leader 地址来自 broker 元数据",cluster());
        try(Admin admin=admin()) {
            e.step("CREATE_TOPIC","createTopics","创建本次实验独立的多分区 topic；副本数不能超过可用 broker 数",Map.of("topic",e.topic,"partitions",partitionCount,"replicas",replicas));
            admin.createTopics(List.of(new NewTopic(e.topic,partitionCount,replicas))).all().get(8,TimeUnit.SECONDS);
        }
        e.step("METADATA","describeTopics / listOffsets","读取 leader、replicas、ISR 和初始 offset",topic(e.topic,null));
        List<Map<String,Object>> produced=new ArrayList<>();
        try(var producer=producer()) {
            for(int i=0;i<count;i++) {
                String key=i<2?"same-key":"key-"+i;
                String value=message+"\nexperiment="+e.id+", sequence="+i;
                Integer partition=i<2?null:(i-2)%partitionCount;
                var record=new ProducerRecord<>(e.topic,partition,null,key,value,List.of(
                        new RecordHeader("experiment-id",e.id.getBytes(StandardCharsets.UTF_8)),
                        new RecordHeader("sequence",Integer.toString(i).getBytes(StandardCharsets.UTF_8))));
                e.step("SERIALIZE","StringSerializer / send","key/value 编码为 UTF-8；前两条由相同 key 路由，其余显式选择分区",Map.of(
                        "sequence",i,"key",key,"value",value,"keyBytes",key.getBytes(StandardCharsets.UTF_8).length,
                        "valueBytes",value.getBytes(StandardCharsets.UTF_8).length,"partitionStrategy",partition==null?"key hash":"explicit partition",
                        "expectedPartition",partition==null?Utils.toPositive(Utils.murmur2(key.getBytes(StandardCharsets.UTF_8)))%partitionCount:partition,
                        "producerConfig",Map.of("acks","all","enable.idempotence",true)));
                var metadata=producer.send(record).get(12,TimeUnit.SECONDS);
                var sent=Map.<String,Object>of("partition",metadata.partition(),"offset",metadata.offset(),"key",key,"value",value,
                        "timestamp",metadata.timestamp(),"serializedKeySize",metadata.serializedKeySize(),"serializedValueSize",metadata.serializedValueSize());
                produced.add(sent);e.step("BROKER_ACK","Future<RecordMetadata>","broker 确认写入后返回实际 partition / offset；这是客户端可观察的结果",sent);
            }
        }
        e.check("sameKeySamePartition",produced.get(0).get("partition").equals(produced.get(1).get("partition")));
        var partitions=new ArrayList<TopicPartition>();for(int p=0;p<partitionCount;p++)partitions.add(new TopicPartition(e.topic,p));
        List<Map<String,Object>> first;
        try(var consumer=consumer(e.group)) {
            prepare(consumer,partitions);e.step("ASSIGN","assign / seekToBeginning","手动分配全部分区，不触发订阅式组内 rebalance；关闭自动提交",position(consumer,partitions));
            first=read(consumer,count,e,"CONSUME_FIRST");
            e.step("UNCOMMITTED","committed / position","消费位置已前进，但没有 commit，已提交位置仍为空；读取并不等于确认处理完成",position(consumer,partitions));
        }
        List<Map<String,Object>> second;
        try(var consumer=consumer(e.group)) {
            prepare(consumer,partitions);second=read(consumer,count,e,"REDELIVERY");
            e.check("uncommittedRecordsRedelivered",sameRecords(first,second));
            var commits=commits(second,e.topic);
            e.step("COMMIT_REQUEST","commitSync(offset + 1)","显式提交每个分区最后已处理消息的 offset + 1，避免把未处理消息一并确认",commitEvidence(commits));
            consumer.commitSync(commits,API_TIMEOUT);
            e.step("COMMIT_ACK","committed","向 broker 查询实际保存的提交位置",position(consumer,partitions));
            var actualCommits=consumer.committed(new HashSet<>(partitions),API_TIMEOUT);
            e.check("committedOffsetsMatch",commits.entrySet().stream()
                    .allMatch(entry->actualCommits.get(entry.getKey())!=null&&entry.getValue().offset()==actualCommits.get(entry.getKey()).offset()));
            var firstSent=produced.getFirst();var tp=new TopicPartition(e.topic,(Integer)firstSent.get("partition"));
            consumer.assign(List.of(tp));consumer.seek(tp,(Long)firstSent.get("offset"));
            var replay=consumer.poll(Duration.ofSeconds(2));
            boolean found=false;for(var record:replay)if(record.offset()==(Long)firstSent.get("offset"))found=true;
            e.check("seekReplayedRecord",found);
            e.step("SEEK_REPLAY","seek / poll","seek 只改变当前消费者位置；这里不提交回退后的 offset",Map.of("partition",tp.partition(),"seekOffset",firstSent.get("offset"),"records",records(replay)));
        }
        try(var consumer=consumer(e.group)) {
            consumer.assign(partitions);var committed=consumer.committed(new HashSet<>(partitions),API_TIMEOUT);
            for(var tp:partitions) {
                if(committed.get(tp)==null)consumer.seekToBeginning(List.of(tp));
                else consumer.seek(tp,committed.get(tp).offset());
            }
            var resumed=consumer.poll(Duration.ofSeconds(1));
            e.check("resumeStartsAfterCommitted",resumed.isEmpty());
            e.step("RESUME","new consumer / committed / seek / poll","新消费者从已提交位置恢复；本次观测窗口没有新消息",Map.of("positions",position(consumer,partitions),"records",records(resumed)));
        }
        try(var consumer=consumer(e.otherGroup)) {
            prepare(consumer,partitions);var independent=read(consumer,count,e,"INDEPENDENT_GROUP");
            e.check("differentGroupReadsAllRecords",sameRecords(first,independent));
        }
        e.check("producedEqualsConsumed",sameRecords(produced,first));
        if(rebalance)rebalance(e,partitions);
        e.step("FINAL_OFFSETS","listOffsets / listConsumerGroupOffsets","对照分区末尾 offset、已提交 offset 和 lag；实验 topic 保留，可继续查询或显式清理",topic(e.topic,e.group));
        e.succeed();
    }

    private static void prepare(KafkaConsumer<String,String> consumer,List<TopicPartition> partitions) {
        consumer.assign(partitions);consumer.seekToBeginning(partitions);
    }
    private static List<Map<String,Object>> read(KafkaConsumer<String,String> consumer,int count,Experiment e,String stage) {
        List<Map<String,Object>> result=new ArrayList<>();long deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(12);
        while(result.size()<count&&System.nanoTime()<deadline&&!Thread.currentThread().isInterrupted()) {
            var batch=consumer.poll(Duration.ofMillis(500));if(batch.isEmpty())continue;
            var rows=records(batch);result.addAll(rows);
            e.step(stage,"poll / StringDeserializer","拉取 broker 消息并反序列化；保留原始 key、value、headers、partition、offset",Map.of("groupId",consumer.groupMetadata().groupId(),"records",rows));
        }
        if(result.size()!=count)throw new IllegalStateException("期望 "+count+" 条，实际消费 "+result.size()+" 条");
        return result;
    }
    private static List<Map<String,Object>> records(ConsumerRecords<String,String> records) {
        List<Map<String,Object>> rows=new ArrayList<>();for(var record:records) {
            var headers=new LinkedHashMap<String,Object>();record.headers().forEach(h->headers.put(h.key(),h.value()==null?null:new String(h.value(),StandardCharsets.UTF_8)));
            var row=new LinkedHashMap<String,Object>();row.put("partition",record.partition());row.put("offset",record.offset());
            row.put("key",record.key());row.put("value",record.value());row.put("timestamp",record.timestamp());
            row.put("headers",headers);rows.add(row);
        }return rows;
    }
    private static List<Map<String,Object>> position(KafkaConsumer<String,String> consumer,List<TopicPartition> partitions) {
        var committed=consumer.committed(new HashSet<>(partitions),API_TIMEOUT);List<Map<String,Object>> rows=new ArrayList<>();
        for(var tp:partitions) {var row=new LinkedHashMap<String,Object>();row.put("partition",tp.partition());
            row.put("position",consumer.position(tp,API_TIMEOUT));row.put("committedOffset",committed.get(tp)==null?null:committed.get(tp).offset());rows.add(row);}
        return rows;
    }
    static Map<TopicPartition,OffsetAndMetadata> commits(List<Map<String,Object>> rows,String topic) {
        Map<TopicPartition,OffsetAndMetadata> result=new LinkedHashMap<>();
        for(var row:rows) {var tp=new TopicPartition(topic,((Number)row.get("partition")).intValue());long next=((Number)row.get("offset")).longValue()+1;
            result.compute(tp,(_key,previous)->new OffsetAndMetadata(Math.max(next,previous==null?0:previous.offset())));}
        return result;
    }
    private static List<Map<String,Object>> commitEvidence(Map<TopicPartition,OffsetAndMetadata> commits) {
        return commits.entrySet().stream().map(entry->Map.<String,Object>of("partition",entry.getKey().partition(),"nextOffset",entry.getValue().offset())).toList();
    }
    static boolean sameRecords(List<Map<String,Object>> left,List<Map<String,Object>> right) {
        if(left.size()!=right.size())return false;
        Map<String,List<Object>> expected=new HashMap<>();for(var row:left)expected.put(row.get("partition")+":"+row.get("offset"),Arrays.asList(row.get("key"),row.get("value")));
        Set<String> seen=new HashSet<>();for(var row:right) {String id=row.get("partition")+":"+row.get("offset");
            if(!seen.add(id)||!Objects.equals(expected.get(id),Arrays.asList(row.get("key"),row.get("value"))))return false;}return true;
    }
    private void rebalance(Experiment e,List<TopicPartition> partitions) {
        try(var first=consumer(e.balanceGroup);var second=consumer(e.balanceGroup)) {
            first.subscribe(List.of(e.topic));second.subscribe(List.of(e.topic));
            e.step("GROUP_JOIN","subscribe","两个消费者使用同一独立 group，通过实际订阅参与 coordinator 分区分配",Map.of("groupId",e.balanceGroup));
            long deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(18);
            while(System.nanoTime()<deadline&&!Thread.currentThread().isInterrupted()) {
                first.poll(Duration.ofMillis(300));second.poll(Duration.ofMillis(300));
                var a=first.assignment();var b=second.assignment();var union=new HashSet<>(a);union.addAll(b);
                if(!a.isEmpty()&&!b.isEmpty()&&Collections.disjoint(a,b)&&union.equals(new HashSet<>(partitions))) {
                    e.check("groupAssignmentsDisjointAndComplete",true);
                    e.step("GROUP_ASSIGNMENT","assignment / groupMetadata","同组两个消费者各持有部分分区，所有分区覆盖且互不重复；这是当前真实分配快照",Map.of(
                            "groupId",e.balanceGroup,"consumerA",a.stream().map(TopicPartition::partition).sorted().toList(),
                            "consumerB",b.stream().map(TopicPartition::partition).sorted().toList(),"generation",first.groupMetadata().generationId()));return;
                }
            }
            throw new IllegalStateException("18 秒内未观测到完整、互斥的双消费者分区分配");
        }
    }
    @PreDestroy public void close() { workers.shutdownNow(); }

    private final class Experiment {
        final String id,topic,group,otherGroup,balanceGroup;
        final Instant started=Instant.now();final long startNanos=System.nanoTime();
        final List<Map<String,Object>> steps=new ArrayList<>();final Map<String,Boolean> checks=new LinkedHashMap<>();
        String status="RUNNING",error;Instant finished;boolean cleaned;
        Experiment(String id) {
            this.id=id;String prefix=properties.topic().replaceAll("[^A-Za-z0-9._-]","-");
            prefix=prefix.substring(0,Math.min(150,prefix.length()));topic=prefix+"-experiment-"+id;
            group="kuma-lab-exp-"+id;otherGroup=group+"-independent";balanceGroup=group+"-balance";
        }
        synchronized boolean running(){return status.equals("RUNNING");}
        synchronized void step(String stage,String operation,String explanation,Object evidence) {
            steps.add(Map.of("sequence",steps.size()+1,"stage",stage,"operation",operation,"explanation",explanation,
                    "occurredAt",Instant.now(),"elapsedMs",(System.nanoTime()-startNanos)/1_000_000,"evidence",evidence));
        }
        synchronized void check(String key,boolean passed){checks.put(key,passed);}
        synchronized void fail(String message){status="FAILED";error=message;step("FAILED","exception","保留已执行步骤和真实错误，后续步骤未执行",Map.of("error",message));}
        synchronized void succeed(){status=checks.values().stream().allMatch(Boolean::booleanValue)?"SUCCEEDED":"FAILED";if(status.equals("FAILED"))error="部分实验断言未通过，请查看 checks";}
        synchronized void finish(){finished=Instant.now();}
        synchronized Map<String,Object> snapshot() {
            var result=new LinkedHashMap<String,Object>();result.put("experimentId",id);result.put("status",status);result.put("topic",topic);
            result.put("groupId",group);result.put("independentGroupId",otherGroup);result.put("balanceGroupId",balanceGroup);
            result.put("startedAt",started);result.put("finishedAt",finished);result.put("cleaned",cleaned);result.put("error",error);
            result.put("steps",List.copyOf(steps));result.put("checks",new LinkedHashMap<>(checks));
            result.put("pollUrl","/lab/kafka/experiments/"+id);result.put("cleanupUrl","/lab/kafka/experiments/"+id);
            result.put("note","记录真实客户端 API 调用及 broker 返回证据，不是 broker 磁盘写入/网络抓包追踪；实验 topic 需显式清理。记录仅在当前 Lab 进程内保留。");return result;
        }
    }
}
