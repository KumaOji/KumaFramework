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
      Objects.requireNonNull(connector, "\u8fde\u63a5\u5668\u4e0d\u80fd\u4e3a\u7a7a!");
      Objects.requireNonNull(config, "\u914d\u7f6e\u4fe1\u606f\u4e0d\u80fd\u4e3a\u7a7a!");
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
            LogUtils.debug("{}: \u4ece canal \u670d\u52a1\u5668\u83b7\u53d6\u6d88\u606f\uff1a >>>>> \u6570:{}", new Object[]{threadName, size});
            if (batchId != -1L && size != 0) {
               this.distributeEvent(message);
            } else {
               LogUtils.debug("{}: \u6ca1\u6709\u4efb\u4f55\u6d88\u606f\u554a\uff0c\u6211\u4f11\u606f{}\u6beb\u79d2", new Object[]{threadName, interval});
               Thread.sleep(interval);
            }

            this.connector.ack(batchId);
            LogUtils.debug("{}: \u786e\u8ba4\u6d88\u606f\u5df2\u88ab\u6d88\u8d39\uff0c\u6d88\u606fID:{}", new Object[]{threadName, batchId});
         } catch (CanalClientException e) {
            --errorCount;
            LogUtils.error(threadName + ": \u53d1\u751f\u9519\u8bef!! ", new Object[]{e});

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
               LogUtils.info("{}: canal \u5ba2\u6237\u7aef\u5df2\u505c\u6b62... ", new Object[]{Thread.currentThread().getName()});
            }

         }
      }

      this.stop();
      LogUtils.info("{}: canal \u5ba2\u6237\u7aef\u5df2\u505c\u6b62. ", new Object[]{Thread.currentThread().getName()});
   }

   protected abstract void distributeEvent(Message message);

   void stop() {
      this.running = false;
   }
}
