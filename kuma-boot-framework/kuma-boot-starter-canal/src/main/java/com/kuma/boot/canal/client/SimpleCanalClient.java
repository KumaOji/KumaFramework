package com.kuma.boot.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.kuma.boot.canal.annotation.ListenPoint;
import com.kuma.boot.canal.autoconfigure.properties.CanalProperties;
import com.kuma.boot.canal.listener.CanalEventListener;
import com.kuma.boot.canal.model.ListenerPoint;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.core.annotation.AnnotatedElementUtils;

public class SimpleCanalClient extends AbstractCanalClient {
   private final ThreadPoolExecutor executor;
   protected final List<CanalEventListener> listeners = new ArrayList();
   private final List<ListenerPoint> annoListeners = new ArrayList();

   public SimpleCanalClient(CanalProperties canalProperties) {
      super(canalProperties);
      this.executor = new ThreadPoolExecutor(5, 20, 120L, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadFactory() {
         private final ThreadFactory factory;

         {
            Objects.requireNonNull(SimpleCanalClient.this);
            this.factory = Executors.defaultThreadFactory();
         }

         public Thread newThread(Runnable r) {
            Thread t = this.factory.newThread(r);
            t.setName("kmccanal-thread-" + t.getName());
            t.setDaemon(true);
            return t;
         }
      });
      this.initListeners();
   }

   protected void process(CanalConnector connector, Map.Entry<String, CanalProperties.Instance> config) {
      this.executor.submit(this.factory.newTransponder(connector, config, this.listeners, this.annoListeners));
   }

   public void stop() {
      super.stop();
      this.executor.shutdown();
   }

   private void initListeners() {
      LogUtils.info("{}: \u76d1\u542c\u5668\u6b63\u5728\u521d\u59cb\u5316....", new Object[]{Thread.currentThread().getName()});
      List<CanalEventListener> list = ContextUtils.getBeansOfTypeWithList(CanalEventListener.class);
      if (list != null) {
         this.listeners.addAll(list);
      }

      Map<String, Object> listenerMap = ContextUtils.getBeansWithAnnotation(com.kuma.boot.canal.annotation.CanalEventListener.class);
      if (listenerMap != null) {
         for(Object target : listenerMap.values()) {
            Method[] methods = target.getClass().getDeclaredMethods();

            for(Method method : methods) {
               ListenPoint lp = (ListenPoint)AnnotatedElementUtils.findMergedAnnotation(method, ListenPoint.class);
               if (lp != null) {
                  this.annoListeners.add(new ListenerPoint(target, method, lp));
               }
            }
         }
      }

      LogUtils.info("{}: \u76d1\u542c\u5668\u521d\u59cb\u5316\u5b8c\u6210.", new Object[]{Thread.currentThread().getName()});
      if (LogUtils.isWarnEnabled() && this.listeners.isEmpty() && this.annoListeners.isEmpty()) {
         LogUtils.warn("{}: \u9879\u76ee\u4e2d\u6ca1\u6709\u4efb\u4f55\u76d1\u542c\u7684\u76ee\u6807! ", new Object[]{Thread.currentThread().getName()});
      }

   }
}
