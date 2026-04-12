package com.kuma.boot.idgenerator.generator;

import com.google.common.collect.Maps;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.ip.IpUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.boot.CommandLineRunner;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public class ZookeeperIdGenerator implements CommandLineRunner {
   private static final byte WORKER_ID_BIT_LENGTH = 16;
   private CuratorFramework curatorFramework;

   public ZookeeperIdGenerator(CuratorFramework curatorFramework) {
      this.curatorFramework = curatorFramework;
   }

   public void run(String... args) throws Exception {
      SnowflakeZookeeper holder = new SnowflakeZookeeper(this.curatorFramework);
      boolean initFlag = holder.init();
      if (initFlag) {
         int workerId = holder.getWorkerId();
         LogUtils.info("\u5f53\u524dID\u751f\u6210\u5668\u7f16\u53f7: " + workerId, new Object[0]);
      }

   }

   public static class SnowflakeZookeeper {
      private String zkAddressNode = null;
      private String listenAddress;
      private int workerId;
      private String port;
      private String projectName;
      private final String PREFIX_ZK_PATH;
      private final String PROP_PATH;
      private final String PATH_FOREVER;
      private String ip;
      private String connectionString;
      private long lastUpdateTime;
      private final CuratorFramework curator;

      public SnowflakeZookeeper(CuratorFramework curator) {
         this.projectName = PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY);
         this.PREFIX_ZK_PATH = "/snowflake/" + this.projectName;
         String var10001 = System.getProperty("java.io.tmpdir");
         this.PROP_PATH = var10001 + File.separator + this.projectName + "/leafconf/{port}/workerID.properties";
         this.PATH_FOREVER = this.PREFIX_ZK_PATH + "/forever";
         this.ip = IpUtils.getLocalIp();
         this.curator = curator;
      }

      public SnowflakeZookeeper(String ip, CuratorFramework curator) {
         this.projectName = PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY);
         this.PREFIX_ZK_PATH = "/snowflake/" + this.projectName;
         String var10001 = System.getProperty("java.io.tmpdir");
         this.PROP_PATH = var10001 + File.separator + this.projectName + "/leafconf/{port}/workerID.properties";
         this.PATH_FOREVER = this.PREFIX_ZK_PATH + "/forever";
         this.ip = ip;
         this.curator = curator;
      }

      public SnowflakeZookeeper(String ip, String port, String connectionString) {
         this.projectName = PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY);
         this.PREFIX_ZK_PATH = "/snowflake/" + this.projectName;
         String var10001 = System.getProperty("java.io.tmpdir");
         this.PROP_PATH = var10001 + File.separator + this.projectName + "/leafconf/{port}/workerID.properties";
         this.PATH_FOREVER = this.PREFIX_ZK_PATH + "/forever";
         this.ip = ip;
         this.port = port;
         this.listenAddress = ip + ":" + port;
         this.connectionString = connectionString;
         CuratorFramework curator = this.createWithOptions(connectionString, new RetryUntilElapsed(1000, 4), 10000, 6000);
         curator.start();
         this.curator = curator;
      }

      public Boolean init() {
         try {
            Stat stat = (Stat)this.curator.checkExists().forPath(this.PATH_FOREVER);
            if (stat == null) {
               this.zkAddressNode = this.createNode(this.curator);
               this.updateLocalWorkerID(this.workerId);
               this.ScheduledUploadData(this.curator, this.zkAddressNode);
               return true;
            }

            Map<String, Integer> nodeMap = Maps.newHashMap();
            Map<String, String> realNode = Maps.newHashMap();

            for(String key : (List<String>)this.curator.getChildren().forPath(this.PATH_FOREVER)) {
               String[] nodeKey = key.split("-");
               realNode.put(nodeKey[0], key);
               nodeMap.put(nodeKey[0], Integer.parseInt(nodeKey[1]));
            }

            Integer workerId = (Integer)nodeMap.get(this.listenAddress);
            if (workerId != null) {
               String var10001 = this.PATH_FOREVER;
               this.zkAddressNode = var10001 + "/" + (String)realNode.get(this.listenAddress);
               this.workerId = workerId;
               if (!this.checkInitTimeStamp(this.curator, this.zkAddressNode)) {
                  throw new RuntimeException("init timestamp check error,forever node timestamp gt this node time");
               }

               this.doService(this.curator);
               this.updateLocalWorkerID(this.workerId);
               LogUtils.info("[Old NODE]find forever node have this endpoint ip-{} port-{} workid-{} childnode and start SUCCESS", new Object[]{this.ip, this.port, this.workerId});
            } else {
               String newNode = this.createNode(this.curator);
               this.zkAddressNode = newNode;
               String[] nodeKey = newNode.split("-");
               this.workerId = Integer.parseInt(nodeKey[1]);
               this.doService(this.curator);
               this.updateLocalWorkerID(this.workerId);
               LogUtils.info("[New NODE]can not find node on forever node that endpoint ip-{} port-{} workid-{},create own node on forever node and start SUCCESS ", new Object[]{this.ip, this.port, this.workerId});
            }
         } catch (Exception e) {
            LogUtils.error("Start node ERROR {}", new Object[]{e});

            try {
               Properties properties = new Properties();
               properties.load(new FileInputStream(new File(this.PROP_PATH.replace("{port}", "" + this.port))));
               this.workerId = Integer.parseInt(properties.getProperty("workerID"));
               LogUtils.warn("START FAILED ,use local node file properties workerID-{}", new Object[]{this.workerId});
            } catch (Exception e1) {
               LogUtils.error("Read file error ", new Object[]{e1});
               return false;
            }
         }

         return true;
      }

      private void doService(CuratorFramework curator) {
         this.ScheduledUploadData(curator, this.zkAddressNode);
      }

      private void ScheduledUploadData(final CuratorFramework curator, final String zk_AddressNode) {
         Executors.newSingleThreadScheduledExecutor((r) -> {
            Thread thread = new Thread(r, "schedule-upload-time");
            thread.setDaemon(true);
            return thread;
         }).scheduleWithFixedDelay(() -> this.updateNewData(curator, zk_AddressNode), 1L, 3L, TimeUnit.SECONDS);
      }

      private boolean checkInitTimeStamp(CuratorFramework curator, String zk_AddressNode) throws Exception {
         byte[] bytes = (byte[])curator.getData().forPath(zk_AddressNode);
         Endpoint endPoint = this.deBuildData(new String(bytes));
         return endPoint.getTimestamp() <= System.currentTimeMillis();
      }

      private String createNode(CuratorFramework curator) throws Exception {
         try {
            return (String)((ACLBackgroundPathAndBytesable)curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL)).forPath(this.PATH_FOREVER + "/" + this.listenAddress + "-", this.buildData().getBytes());
         } catch (Exception e) {
            LogUtils.error("create node error msg {} ", new Object[]{e.getMessage()});
            throw e;
         }
      }

      private void updateNewData(CuratorFramework curator, String path) {
         try {
            if (System.currentTimeMillis() < this.lastUpdateTime) {
               return;
            }

            curator.setData().forPath(path, this.buildData().getBytes());
            this.lastUpdateTime = System.currentTimeMillis();
         } catch (Exception e) {
            LogUtils.info("update init data error path is {} error is {}", new Object[]{path, e});
         }

      }

      private String buildData() throws JacksonException {
         Endpoint endpoint = new Endpoint(this.ip, this.port, System.currentTimeMillis());
         JsonMapper jsonMapper = JsonMapper.builder().build();
         return jsonMapper.writeValueAsString(endpoint);
      }

      private Endpoint deBuildData(String json) throws IOException {
         JsonMapper jsonMapper = JsonMapper.builder().build();
         return (Endpoint)jsonMapper.readValue(json, Endpoint.class);
      }

      private void updateLocalWorkerID(int workerID) {
         File leafConfFile = new File(this.PROP_PATH.replace("{port}", this.port));
         boolean exists = leafConfFile.exists();
         LogUtils.info("file exists status is {}", new Object[]{exists});
         if (exists) {
            try {
               FileUtils.writeStringToFile(leafConfFile, "workerID=" + workerID, StandardCharsets.UTF_8);
               LogUtils.info("update file cache workerID is {}", new Object[]{workerID});
            } catch (IOException e) {
               LogUtils.error("update file cache error ", new Object[]{e});
            }
         } else {
            try {
               boolean mkdirs = leafConfFile.getParentFile().mkdirs();
               LogUtils.info("init local file cache create parent dis status is {}, worker id is {}", new Object[]{mkdirs, workerID});
               if (mkdirs) {
                  if (leafConfFile.createNewFile()) {
                     FileUtils.writeStringToFile(leafConfFile, "workerID=" + workerID, StandardCharsets.UTF_8);
                     LogUtils.info("local file cache workerID is {}", new Object[]{workerID});
                  }
               } else {
                  LogUtils.warn("create parent dir error===", new Object[0]);
               }
            } catch (IOException e) {
               LogUtils.warn("craete workerID conf file error", new Object[]{e});
            }
         }

      }

      private CuratorFramework createWithOptions(String connectionString, RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs) {
         return CuratorFrameworkFactory.builder().connectString(connectionString).retryPolicy(retryPolicy).connectionTimeoutMs(connectionTimeoutMs).sessionTimeoutMs(sessionTimeoutMs).build();
      }

      public String getZkAddressNode() {
         return this.zkAddressNode;
      }

      public void setZkAddressNode(String zkAddressNode) {
         this.zkAddressNode = zkAddressNode;
      }

      public String getListenAddress() {
         return this.listenAddress;
      }

      public void setListenAddress(String listenAddress) {
         this.listenAddress = listenAddress;
      }

      public int getWorkerId() {
         return this.workerId;
      }

      public void setWorkerId(int workerId) {
         this.workerId = workerId;
      }

      public String getPort() {
         return this.port;
      }

      public void setPort(String port) {
         this.port = port;
      }

      public String getProjectName() {
         return this.projectName;
      }

      public void setProjectName(String projectName) {
         this.projectName = projectName;
      }

      public String getPREFIX_ZK_PATH() {
         return this.PREFIX_ZK_PATH;
      }

      public String getPROP_PATH() {
         return this.PROP_PATH;
      }

      public String getPATH_FOREVER() {
         return this.PATH_FOREVER;
      }

      public String getIp() {
         return this.ip;
      }

      public void setIp(String ip) {
         this.ip = ip;
      }

      public String getConnectionString() {
         return this.connectionString;
      }

      public void setConnectionString(String connectionString) {
         this.connectionString = connectionString;
      }

      public long getLastUpdateTime() {
         return this.lastUpdateTime;
      }

      public void setLastUpdateTime(long lastUpdateTime) {
         this.lastUpdateTime = lastUpdateTime;
      }

      public static class Endpoint {
         private String ip;
         private String port;
         private long timestamp;

         public Endpoint() {
         }

         public Endpoint(String ip, String port, long timestamp) {
            this.ip = ip;
            this.port = port;
            this.timestamp = timestamp;
         }

         public String getIp() {
            return this.ip;
         }

         public void setIp(String ip) {
            this.ip = ip;
         }

         public String getPort() {
            return this.port;
         }

         public void setPort(String port) {
            this.port = port;
         }

         public long getTimestamp() {
            return this.timestamp;
         }

         public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
         }
      }
   }
}
