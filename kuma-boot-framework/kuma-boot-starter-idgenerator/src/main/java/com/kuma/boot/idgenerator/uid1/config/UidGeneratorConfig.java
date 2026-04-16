package com.kuma.boot.idgenerator.uid1.config;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.idgenerator.uid1.impl.CachedUidGenerator;
import com.kuma.boot.idgenerator.uid1.impl.DefaultUidGenerator;
import com.kuma.boot.idgenerator.uid1.worker.WorkerIdAssigner;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "kuma.boot.idgenerator", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(
   basePackages = {"com.kuma.boot.idgenerator.uid1"}
)
@EnableConfigurationProperties({UidGeneratorProperties.class})
public class UidGeneratorConfig {
   @Resource
   UidGeneratorProperties uidGeneratorProperties;

   public UidGeneratorConfig() {
   }

   @Bean
   public CachedUidGenerator cachedUidGenerator(WorkerIdAssigner disposableWorkerIdAssigner) {
      CachedUidGenerator cachedUidGenerator = new CachedUidGenerator();
      cachedUidGenerator.setWorkerIdAssigner(disposableWorkerIdAssigner);
      this.setBaseProp(cachedUidGenerator);
      this.setCachedProp(cachedUidGenerator);
      return cachedUidGenerator;
   }

   void setBaseProp(DefaultUidGenerator defaultUidGenerator) {
      if (this.uidGeneratorProperties != null) {
         if (this.uidGeneratorProperties.getTimeBits() != null) {
            defaultUidGenerator.setTimeBits(this.uidGeneratorProperties.getTimeBits());
         }

         if (this.uidGeneratorProperties.getWorkerBits() != null) {
            defaultUidGenerator.setWorkerBits(this.uidGeneratorProperties.getWorkerBits());
         }

         if (this.uidGeneratorProperties.getSeqBits() != null) {
            defaultUidGenerator.setSeqBits(this.uidGeneratorProperties.getSeqBits());
         }

         if (StringUtils.isNotBlank(this.uidGeneratorProperties.getEpochStr())) {
            defaultUidGenerator.setEpochStr(this.uidGeneratorProperties.getEpochStr());
         }

      }
   }

   void setCachedProp(CachedUidGenerator cachedUidGenerator) {
      if (this.uidGeneratorProperties != null) {
         if (this.uidGeneratorProperties.getBoostPower() != null) {
            cachedUidGenerator.setBoostPower(this.uidGeneratorProperties.getBoostPower());
         }

         if (this.uidGeneratorProperties.getScheduleInterval() != null) {
            cachedUidGenerator.setScheduleInterval(this.uidGeneratorProperties.getScheduleInterval());
         }

      }
   }
}
