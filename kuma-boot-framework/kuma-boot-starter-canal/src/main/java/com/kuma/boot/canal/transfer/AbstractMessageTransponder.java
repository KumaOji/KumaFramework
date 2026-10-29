package com.kuma.boot.canal.transfer;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.kuma.boot.canal.autoconfigure.properties.CanalProperties;
import com.kuma.boot.canal.listener.CanalEventListener;
import com.kuma.boot.canal.model.ListenerPoint;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractMessageTransponder implements MessageTransponder {
   private final CanalConnector connector;
   protected final CanalProperties.Instance config;
   protected final String destination;
   protected final List<CanalEventListener> implListeners = new ArrayList();
   protected final List<ListenerPoint> annotationListeners = new ArrayList();
   private volatile boolean running = true;

   public AbstractMessageTransponder(CanalConnector connector, Map.Entry<String, CanalProperties.Instance> config, List<CanalEventListener> implListeners, List<ListenerPoint> annotationListeners) {
      Objects.requireNonNull(connector, "连接器不能为空!");
      Objects.requireNonNull(config, "配置信息不能为空!");
      this.connector = connector;
      this.destination = (String)config.getKey();
      this.config = (CanalProperties.Instance)config.getValue();
      if (implListeners != null) {
         this.implListeners.addAll(implListeners);
      }

      if (annotationListeners != null) {
         this.annotationListeners.addAll(annotationListeners);
      }

   }

   public void run() {
      int errorCount = this.config.getRetryCount();
      long interval = this.config.getAcquireInterval();
      String threadName = Thread.currentThread().getName();

      while(this.running && !Thread.currentThread().isInterrupted()) {
         try {
            Message message = this.connector.getWithoutAck(this.config.getBatchSize());
            long batchId = message.getId();
            int size = message.getEntries().size();
            LogUtils.debug("{}: 从 canal 服务器获取消息： >>>>> 数:{}", new Object[]{threadName, size});
            if (batchId != -1L && size != 0) {
               this.distributeEvent(message);
            } else {
               LogUtils.debug("{}: 没有任何消息啊，我休息{}毫秒", new Object[]{threadName, interval});
               Thread.sleep(interval);
            }

            this.connector.ack(batchId);
            LogUtils.debug("{}: 确认消息已被消费，消息ID:{}", new Object[]{threadName, batchId});
         } catch (CanalClientException e) {
            --errorCount;
            LogUtils.error(threadName + ": 发生错误!! ", new Object[]{e});

            try {
               Thread.sleep(interval);
            } catch (InterruptedException var14) {
               errorCount = 0;
            }
         } catch (InterruptedException var16) {
            errorCount = 0;
            this.connector.rollback();
         } finally {
            if (errorCount <= 0) {
               this.stop();
               LogUtils.info("{}: canal 客户端已停止... ", new Object[]{Thread.currentThread().getName()});
            }

         }
      }

      this.stop();
      LogUtils.info("{}: canal 客户端已停止. ", new Object[]{Thread.currentThread().getName()});
   }

   protected abstract void distributeEvent(Message message);

   void stop() {
      this.running = false;
   }
}
