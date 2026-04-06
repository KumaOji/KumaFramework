package com.kuma.boot.seata.spi.metrics;

import java.io.IOException;
import org.apache.seata.common.loader.LoadLevel;
import org.apache.seata.metrics.exporter.Exporter;
import org.apache.seata.metrics.registry.Registry;

@LoadLevel(
   name = "kmcExporter",
   order = 1
)
public class KmcExporter implements Exporter {
   public void setRegistry(Registry registry) {
   }

   public void close() throws IOException {
   }
}
