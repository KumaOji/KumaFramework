package com.kuma.boot.idgenerator.uid.worker;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idgenerator.uid.utils.DockerUtils;
import com.kuma.boot.idgenerator.uid.utils.NetUtils;
import com.kuma.boot.idgenerator.uid.worker.dao.WorkerNodeDAO;
import com.kuma.boot.idgenerator.uid.worker.entity.WorkerNodeEntity;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DisposableWorkerIdAssigner implements WorkerIdAssigner {
   @Resource
   private WorkerNodeDAO workerNodeDAO;

   public DisposableWorkerIdAssigner() {
   }

   @Transactional
   public long assignWorkerId() {
      WorkerNodeEntity workerNodeEntity = this.buildWorkerNode();
      this.workerNodeDAO.addWorkerNode(workerNodeEntity);
      LogUtils.info("[uid-gen] Add worker node:" + String.valueOf(workerNodeEntity), new Object[0]);
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
