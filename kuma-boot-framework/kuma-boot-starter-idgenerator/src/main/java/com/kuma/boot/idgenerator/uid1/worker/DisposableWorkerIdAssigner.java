package com.kuma.boot.idgenerator.uid1.worker;

import com.kuma.boot.idgenerator.uid1.utils.DockerUtils;
import com.kuma.boot.idgenerator.uid1.utils.NetUtils;
import com.kuma.boot.idgenerator.uid1.worker.entity.WorkerNodeEntity;
import com.kuma.boot.idgenerator.uid1.worker.repository.WorkerNodeResposity;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("uid1DisposableWorkerIdAssigner")
public class DisposableWorkerIdAssigner implements WorkerIdAssigner {
   private static final Logger LOGGER = LoggerFactory.getLogger(DisposableWorkerIdAssigner.class);
   private final WorkerNodeResposity workerNodeResposity;

   public DisposableWorkerIdAssigner(WorkerNodeResposity workerNodeResposity) {
      this.workerNodeResposity = workerNodeResposity;
   }

   // 底层为 JdbcTemplate，勿加 @Transactional：否则会走 JPA 事务，Hibernate 多租户启动阶段无 tenant 会失败。
   public long assignWorkerId() {
      WorkerNodeEntity workerNodeEntity = this.buildWorkerNode();
      this.workerNodeResposity.addWorkerNode(workerNodeEntity);
      LOGGER.info("Add worker node:" + String.valueOf(workerNodeEntity));
      return workerNodeEntity.getId();
   }

   private WorkerNodeEntity buildWorkerNode() {
      WorkerNodeEntity workerNodeEntity = new WorkerNodeEntity();
      if (DockerUtils.isDocker()) {
         workerNodeEntity.setType(WorkerNodeType.CONTAINER.value());
         workerNodeEntity.setHostName(DockerUtils.getDockerHost());
         workerNodeEntity.setPort(DockerUtils.getDockerPort());
      } else {
         workerNodeEntity.setType(WorkerNodeType.ACTUAL.value());
         workerNodeEntity.setHostName(NetUtils.getLocalAddress());
         long var10001 = System.currentTimeMillis();
         workerNodeEntity.setPort(var10001 + "-" + RandomUtils.nextInt(0, 100000));
      }

      return workerNodeEntity;
   }
}
