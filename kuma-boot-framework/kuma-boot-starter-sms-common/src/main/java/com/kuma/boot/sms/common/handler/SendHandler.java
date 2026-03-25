package com.kuma.boot.sms.common.handler;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sms.common.model.NoticeData;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public interface SendHandler {
   boolean acceptSend(String type);

   boolean send(NoticeData noticeData, Collection phones);

   default boolean send(NoticeData noticeData, String phone) {
      return StringUtils.isBlank(phone) ? false : this.send(noticeData, (Collection)Collections.singletonList(phone));
   }

   default boolean send(NoticeData noticeData, String... phones) {
      return phones == null ? false : this.send(noticeData, (Collection)Arrays.asList(phones));
   }
}
