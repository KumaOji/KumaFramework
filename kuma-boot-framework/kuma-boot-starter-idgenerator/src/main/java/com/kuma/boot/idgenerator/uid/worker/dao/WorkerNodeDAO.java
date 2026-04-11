package com.kuma.boot.idgenerator.uid.worker.dao;

import com.kuma.boot.idgenerator.uid.worker.entity.WorkerNodeEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WorkerNodeDAO {
   @Select({"SELECT ID,HOST_NAME,PORT,TYPE,LAUNCH_DATE,MODIFIED,CREATEDFROM WORKER_NODE WHERE HOST_NAME = #{host} AND PORT = #{port}"})
   WorkerNodeEntity getWorkerNodeByHostPort(@Param("host") String host, @Param("port") String port);

   @Insert({"INSERT INTO WORKER_NODE(HOST_NAME,PORT,TYPE,LAUNCH_DATE,MODIFIED,CREATED)VALUES (#{hostName},#{port},#{type},#{launchDate},NOW(),NOW())"})
   void addWorkerNode(WorkerNodeEntity workerNodeEntity);
}
