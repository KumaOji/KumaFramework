package com.kuma.boot.monitor.endpoint;

import cn.hutool.json.JSONObject;
import com.kuma.boot.monitor.collect.HealthCheckProvider;
import com.kuma.boot.monitor.model.Report;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(
   id = "kmcmonitor"
)
public class MonitorEndPoint {
   private final HealthCheckProvider healthCheckProvider;

   public MonitorEndPoint(HealthCheckProvider healthCheckProvider) {
      this.healthCheckProvider = healthCheckProvider;
   }

   @ReadOperation
   public JSONObject healthCheckProvider() {
      Report report = this.healthCheckProvider.getReport(false);
      JSONObject jsonObject = new JSONObject();
      jsonObject.set("report", report);
      return jsonObject;
   }
}
