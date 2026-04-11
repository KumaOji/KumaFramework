package com.kuma.boot.idgenerator.uid1.worker.repository;

import com.kuma.boot.idgenerator.uid1.worker.entity.WorkerNodeEntity;

public interface WorkerNodeResposity {
   WorkerNodeEntity getWorkerNodeByHostPort(String host, String port);

   void addWorkerNode(WorkerNodeEntity entity);
}
