package com.kuma.boot.sms.common.service;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sms.common.model.NoticeData;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public interface NoticeService {
   boolean phoneRegValidation(String phone);

   boolean send(NoticeData noticeData, Collection phones);

   default boolean send(NoticeData noticeData, String phone) {
      return StringUtils.isBlank(phone) ? false : this.send(noticeData, (Collection)Collections.singletonList(phone));
   }

   default boolean send(NoticeData noticeData, String... phones) {
      return phones.length <= 0 ? false : this.send(noticeData, (Collection)Arrays.asList(phones));
   }

   void asyncSend(NoticeData noticeData, Collection phones);

   default void asyncSend(NoticeData noticeData, String phone) {
      if (!StringUtils.isBlank(phone)) {
         this.asyncSend(noticeData, (Collection)Collections.singletonList(phone));
      }

   }

   default void asyncSend(NoticeData noticeData, String... phones) {
      if (phones.length > 0) {
         this.asyncSend(noticeData, (Collection)Arrays.asList(phones));
      }

   }
}
