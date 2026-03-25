package com.kuma.boot.sms.common.model;

import java.util.Collection;

public class NoticeInfo {
   private NoticeData noticeData;
   private Collection phones;

   public NoticeData getNoticeData() {
      return this.noticeData;
   }

   public void setNoticeData(NoticeData noticeData) {
      this.noticeData = noticeData;
   }

   public Collection getPhones() {
      return this.phones;
   }

   public void setPhones(Collection phones) {
      this.phones = phones;
   }
}
