package com.kuma.boot.eventbus.atlas.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaDistributedEventBus implements EventBus {
   private final EventBus localEventBus;
   private final String nodeId;
   private final String kafkaTopic;
   private final KafkaProducer kafkaProducer;
   private final KafkaConsumer kafkaConsumer;
   private final ExecutorService executorService;
   private volatile boolean running = true;

   public KafkaDistributedEventBus(EventBus localEventBus, String bootstrapServers, String topic, String groupId) {
      this.localEventBus = localEventBus;
      this.nodeId = UUID.randomUUID().toString();
      this.kafkaTopic = topic;
      Properties producerProps = new Properties();
      producerProps.put("bootstrap.servers", bootstrapServers);
      producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      this.kafkaProducer = new KafkaProducer(producerProps);
      Properties consumerProps = new Properties();
      consumerProps.put("bootstrap.servers", bootstrapServers);
      consumerProps.put("group.id", groupId);
      consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      consumerProps.put("auto.offset.reset", "latest");
      this.kafkaConsumer = new KafkaConsumer(consumerProps);
      this.executorService = Executors.newSingleThreadExecutor();
      this.executorService.submit(this::consumeEvents);
   }

   public void publish(Event event) {
      this.localEventBus.publish(event);
      if (event instanceof DistributedEvent distributedEvent) {
         distributedEvent.setSourceNodeId(this.nodeId);
         distributedEvent.setProcessedLocally(true);
         String targetNodeId = distributedEvent.getTargetNodeId();
         if (targetNodeId != null && !targetNodeId.isEmpty()) {
            this.publishToKafka(distributedEvent, targetNodeId);
         } else {
            this.publishToKafka(distributedEvent);
         }
      }

   }

   private void publishToKafka(DistributedEvent event) {
      try {
         String eventJson = this.serializeEvent(event);
         ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.kafkaTopic, event.getId(), eventJson);
         this.kafkaProducer.send(record);
      } catch (Exception e) {
         System.err.println("Error publishing event to Kafka: " + e.getMessage());
         e.printStackTrace();
      }

   }

   private void publishToKafka(DistributedEvent event, String targetNodeId) {
      try {
         String eventJson = this.serializeEvent(event);
         ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.kafkaTopic, targetNodeId, eventJson);
         this.kafkaProducer.send(record);
      } catch (Exception e) {
         System.err.println("Error publishing event to Kafka: " + e.getMessage());
         e.printStackTrace();
      }

   }

   private void consumeEvents() {
      try {
         this.kafkaConsumer.subscribe(Collections.singletonList(this.kafkaTopic));

         while(this.running) {
            for(ConsumerRecord<String, String> record : this.kafkaConsumer.poll(KafkaDistributedEventBus.Duration.ofMillis(100L))) {
               String key = record.key();
               if (key == null || key.equals(this.nodeId) || key.isEmpty()) {
                  try {
                     DistributedEvent event = this.deserializeEvent(record.value());
                     if (!this.nodeId.equals(event.getSourceNodeId())) {
                        this.receiveRemoteEvent(event);
                     }
                  } catch (Exception e) {
                     System.err.println("Error processing Kafka message: " + e.getMessage());
                     e.printStackTrace();
                  }
               }
            }
         }
      } catch (Exception e) {
         if (this.running) {
            System.err.println("Error in Kafka consumer: " + e.getMessage());
            e.printStackTrace();
         }
      } finally {
         this.kafkaConsumer.close();
      }

   }

   public void receiveRemoteEvent(DistributedEvent event) {
      event.setProcessedLocally(false);
      this.localEventBus.publish(event);
   }

   private String serializeEvent(DistributedEvent event) {
      String var10000 = event.getId();
      return "{ \"id\": \"" + var10000 + "\", \"type\": \"" + event.getType() + "\", \"sourceNodeId\": \"" + event.getSourceNodeId() + "\", \"processedLocally\": " + event.isProcessedLocally() + " }";
   }

   private DistributedEvent deserializeEvent(String json) {
      return new SimpleDistributedEvent();
   }

   public void register(Object listener) {
      this.localEventBus.register(listener);
   }

   public void unregister(Object listener) {
      this.localEventBus.unregister(listener);
   }

   public void scanAndRegister(Object listener) {
      this.localEventBus.scanAndRegister(listener);
   }

   public void shutdown() {
      this.running = false;
      this.executorService.shutdown();

      try {
         if (!this.executorService.awaitTermination(5L, TimeUnit.SECONDS)) {
            this.executorService.shutdownNow();
         }
      } catch (InterruptedException var2) {
         this.executorService.shutdownNow();
         Thread.currentThread().interrupt();
      }

      this.kafkaProducer.close();
   }

   public String getNodeId() {
      return this.nodeId;
   }

   private static class KafkaProducer {
      public KafkaProducer(Properties properties) {
      }

      public void send(ProducerRecord<String, String> record) {
      }

      public void close() {
      }
   }

   private static class KafkaConsumer {
      public KafkaConsumer(Properties properties) {
      }

      public void subscribe(List<String> topics) {
      }

      public ConsumerRecords<String, String> poll(Duration duration) {
         return new ConsumerRecords<String, String>();
      }

      public void close() {
      }
   }

   private static class ProducerRecord<K, V> {
      private final String topic;
      private final K key;
      private final V value;

      public ProducerRecord(String topic, K key, V value) {
         this.topic = topic;
         this.key = key;
         this.value = value;
      }
   }

   private static class ConsumerRecord<K, V> {
      private final K key;
      private final V value;

      public ConsumerRecord(K key, V value) {
         this.key = key;
         this.value = value;
      }

      public K key() {
         return this.key;
      }

      public V value() {
         return this.value;
      }
   }

   private static class ConsumerRecords<K, V> implements Iterable<ConsumerRecord<K, V>> {
      private ConsumerRecords() {
      }

      public Iterator<ConsumerRecord<K, V>> iterator() {
         return Collections.emptyIterator();
      }
   }

   private static class Duration {
      private Duration() {
      }

      public static Duration ofMillis(long millis) {
         return new Duration();
      }
   }

   private static class SimpleDistributedEvent implements DistributedEvent {
      private String id = UUID.randomUUID().toString();
      private String type = "simple.distributed.event";
      private String sourceNodeId;
      private String targetNodeId;
      private boolean processedLocally;
      private Map<String, Object> data = new HashMap();
      private long timestamp = System.currentTimeMillis();
      private Map<String, Object> metadata = new HashMap();

      private SimpleDistributedEvent() {
      }

      public String getId() {
         return this.id;
      }

      public String getType() {
         return this.type;
      }

      public Map<String, Object> getData() {
         return this.data;
      }

      public long getTimestamp() {
         return this.timestamp;
      }

      public Map<String, Object> getMetadata() {
         return this.metadata;
      }

      public String getTargetNodeId() {
         return this.targetNodeId;
      }

      public String getSourceNodeId() {
         return this.sourceNodeId;
      }

      public void setSourceNodeId(String sourceNodeId) {
         this.sourceNodeId = sourceNodeId;
      }

      public boolean isProcessedLocally() {
         return this.processedLocally;
      }

      public void setProcessedLocally(boolean processedLocally) {
         this.processedLocally = processedLocally;
      }
   }
}
