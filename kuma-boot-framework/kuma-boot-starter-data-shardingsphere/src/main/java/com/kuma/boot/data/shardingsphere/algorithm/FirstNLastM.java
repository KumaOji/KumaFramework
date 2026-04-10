package com.kuma.boot.data.shardingsphere.algorithm;

import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithmMetaData;
import org.apache.shardingsphere.infra.algorithm.core.config.AlgorithmConfiguration;
import org.apache.shardingsphere.infra.algorithm.core.context.AlgorithmSQLContext;

public class FirstNLastM implements EncryptAlgorithm {
   public FirstNLastM() {
   }

   public Object encrypt(Object o, AlgorithmSQLContext algorithmSQLContext) {
      return null;
   }

   public Object decrypt(Object o, AlgorithmSQLContext algorithmSQLContext) {
      return null;
   }

   public EncryptAlgorithmMetaData getMetaData() {
      return null;
   }

   public AlgorithmConfiguration toConfiguration() {
      return null;
   }
}
