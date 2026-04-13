package com.kuma.boot.idgenerator.uid1.worker;

import com.kuma.boot.idgenerator.uid1.utils.DockerUtils;
import com.kuma.boot.idgenerator.uid1.utils.NetUtils;
import com.kuma.boot.idgenerator.uid1.worker.entity.WorkerNodeEntity;
import com.kuma.boot.idgenerator.uid1.worker.repository.WorkerNodeResposity;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("uid1DisposableWorkerIdAssigner")
public class DisposableWorkerIdAssigner implements WorkerIdAssigner {
   private static final Logger LOGGER = LoggerFactory.getLogger(DisposableWorkerIdAssigner.class);
   private final WorkerNodeResposity workerNodeResposity;

   public DisposableWorkerIdAssigner(WorkerNodeResposity workerNodeResposity) {
      this.workerNodeResposity = workerNodeResposity;
   }

   @Transactional
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
