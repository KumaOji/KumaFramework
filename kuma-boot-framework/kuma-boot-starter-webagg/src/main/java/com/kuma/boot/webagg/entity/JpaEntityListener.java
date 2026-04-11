package com.kuma.boot.webagg.entity;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class JpaEntityListener {
   public JpaEntityListener() {
   }

   @PrePersist
   public void prePersist(Object object) {
      LogUtils.info(" AbstractListener prePersis: {}", new Object[]{object});
   }

   @PostPersist
   public void postPersist(Object object) {
      LogUtils.info("AbstractListener postPersist: {}", new Object[]{object});
   }

   @PostLoad
   public void postLoad(Object object) {
      LogUtils.info("AbstractListener postLoad: {}", new Object[]{object});
   }

   @PreUpdate
   public void preUpdate(Object object) {
      LogUtils.info("AbstractListener preUpdate: {}", new Object[]{object});
   }

   @PostUpdate
   public void postUpdate(Object object) {
      LogUtils.info("AbstractListener postUpdate: {}", new Object[]{object});
   }

   @PreRemove
   public void preRemove(Object object) {
      LogUtils.info("AbstractListener preRemove: {}", new Object[]{object});
   }

   @PostRemove
   public void postRemove(Object object) {
      LogUtils.info("AbstractListener postRemove: {}", new Object[]{object});
   }

   @PreDestroy
   public void preDestroy(Object object) {
      LogUtils.info("AbstractListener preDestroy: {}", new Object[]{object});
   }
}
