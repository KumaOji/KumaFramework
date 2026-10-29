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
      Objects.requireNonNull(canalProperties, "canalConfig 不能为空!");
      Objects.requireNonNull(canalProperties, "transponderFactory 不能为空!");
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
               throw new CanalClientException("zookeeper 地址格式不正确，应该为 ip:port....:" + s);
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
         throw new CanalClientException("无法解析 canal 的连接信息，请联系开发人员!");
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
