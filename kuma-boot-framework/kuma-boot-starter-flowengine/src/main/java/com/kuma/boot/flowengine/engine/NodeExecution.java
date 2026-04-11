package com.kuma.boot.flowengine.engine;

import com.kuma.boot.flowengine.module.FlowNode;
import java.sql.Timestamp;

public interface NodeExecution {
   void execute();

   FlowNode currentNode();

   String decision();

   Timestamp getEndTime();

   Timestamp getStartTime();

   Throwable getError();

   ExecutionStatus getStatus();
}
