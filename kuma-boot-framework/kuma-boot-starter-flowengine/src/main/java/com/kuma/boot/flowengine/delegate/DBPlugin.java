package com.kuma.boot.flowengine.delegate;

import java.sql.Timestamp;

public interface DBPlugin {
   Timestamp currentTimestamp();

   Long nextVar(String sequenceName);

   void lock(String policy, String module, String lockName);

   void lockNoWaite(String policy, String module, String lockName);
}
