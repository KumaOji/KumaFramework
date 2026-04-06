package com.kuma.boot.seata.spi.discovery;

import org.apache.seata.common.loader.LoadLevel;
import org.apache.seata.discovery.registry.RegistryProvider;
import org.apache.seata.discovery.registry.RegistryService;

@LoadLevel(
   name = "File",
   order = 1
)
public class KmcRegistryProvider implements RegistryProvider {
   public RegistryService provide() {
      return null;
   }
}
