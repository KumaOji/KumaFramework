package com.kuma.boot.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.kuma.boot.canal.autoconfigure.properties.CanalProperties;
import com.kuma.boot.canal.transfer.DefaultMessageTransponder;
import com.kuma.boot.canal.transfer.TransponderFactory;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractCanalClient implements CanalClient {
   private volatile boolean running;
   private final CanalProperties canalProperties;
   protected final TransponderFactory factory;

   protected AbstractCanalClient(CanalProperties canalProperties) {
      Objects.requireNonNull(canalProperties, "canalConfig \u4e0d\u80fd\u4e3a\u7a7a!");
      Objects.requireNonNull(canalProperties, "transponderFactory \u4e0d\u80fd\u4e3a\u7a7a!");
      this.canalProperties = canalProperties;
      this.factory = DefaultMessageTransponder::new;
   }

   public void start() {
      Map<String, CanalProperties.Instance> instanceMap = this.getConfig();

      for(Map.Entry<String, CanalProperties.Instance> instanceEntry : instanceMap.entrySet()) {
         this.process(this.processInstanceEntry(instanceEntry), instanceEntry);
      }

   }

   protected abstract void process(CanalConnector connector, Map.Entry<String, CanalProperties.Instance> config);

   private CanalConnector processInstanceEntry(Map.Entry<String, CanalProperties.Instance> instanceEntry) {
      CanalProperties.Instance instance = (CanalProperties.Instance)instanceEntry.getValue();
      CanalConnector connector;
      if (instance.getClusterEnabled()) {
         List<SocketAddress> addresses = new ArrayList();

         for(String s : instance.getZookeeperAddress()) {
            String[] entry = s.split(":");
            if (entry.length != 2) {
               throw new CanalClientException("zookeeper \u5730\u5740\u683c\u5f0f\u4e0d\u6b63\u786e\uff0c\u5e94\u8be5\u4e3a ip:port....:" + s);
            }

            addresses.add(new InetSocketAddress(entry[0], Integer.parseInt(entry[1])));
         }

         connector = CanalConnectors.newClusterConnector(addresses, (String)instanceEntry.getKey(), instance.getUserName(), instance.getPassword());
      } else {
         connector = CanalConnectors.newSingleConnector(new InetSocketAddress(instance.getHost(), instance.getPort()), (String)instanceEntry.getKey(), instance.getUserName(), instance.getPassword());
      }

      connector.connect();
      if (!StringUtils.isEmpty(instance.getFilter())) {
         connector.subscribe(instance.getFilter());
      } else {
         connector.subscribe();
      }

      connector.rollback();
      return connector;
   }

   protected Map<String, CanalProperties.Instance> getConfig() {
      CanalProperties config = this.canalProperties;
      Map<String, CanalProperties.Instance> instanceMap;
      if ((instanceMap = config.getInstances()) != null && !instanceMap.isEmpty()) {
         return config.getInstances();
      } else {
         throw new CanalClientException("\u65e0\u6cd5\u89e3\u6790 canal \u7684\u8fde\u63a5\u4fe1\u606f\uff0c\u8bf7\u8054\u7cfb\u5f00\u53d1\u4eba\u5458!");
      }
   }

   public void stop() {
      this.setRunning(false);
   }

   public boolean isRunning() {
      return this.running;
   }

   private void setRunning(boolean running) {
      this.running = running;
   }
}
