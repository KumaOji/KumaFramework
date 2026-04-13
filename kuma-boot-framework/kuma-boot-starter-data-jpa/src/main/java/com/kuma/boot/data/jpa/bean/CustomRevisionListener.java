package com.kuma.boot.data.jpa.bean;

import com.kuma.boot.common.utils.log.LogUtils;
import org.hibernate.envers.RevisionListener;

public class CustomRevisionListener implements RevisionListener {
   public CustomRevisionListener() {
   }

   public void newRevision(Object o) {
      LogUtils.info((String)o, new Object[0]);
   }
}
