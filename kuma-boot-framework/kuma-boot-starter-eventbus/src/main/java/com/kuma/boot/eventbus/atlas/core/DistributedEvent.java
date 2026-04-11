package com.kuma.boot.eventbus.atlas.core;

public interface DistributedEvent extends Event {
   String getTargetNodeId();

   String getSourceNodeId();

   void setSourceNodeId(String sourceNodeId);

   boolean isProcessedLocally();

   void setProcessedLocally(boolean processedLocally);
}
