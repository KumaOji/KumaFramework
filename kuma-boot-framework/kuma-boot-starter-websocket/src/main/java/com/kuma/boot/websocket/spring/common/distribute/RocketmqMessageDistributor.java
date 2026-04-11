package com.kuma.boot.websocket.spring.common.distribute;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.spring.common.exception.ErrorJsonMessageException;
import com.kuma.boot.websocket.spring.common.session.WebSocketSessionStore;
import java.nio.charset.StandardCharsets;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;

@RocketMQMessageListener(
   consumerGroup = "${spring.application.name:default-kuma-application}-${spring.profiles.active:dev}",
   topic = "${spring.application.name:default-kuma-application}-${spring.profiles.active:dev}",
   selectorExpression = "${kuma.cloud.websocket.mq.tag}",
   messageModel = MessageModel.BROADCASTING
)
public class RocketmqMessageDistributor extends AbstractMessageDistributor implements RocketMQListener<MessageExt> {
   @Value("${spring.application.name}")
   private String appName;
   @Value("${kuma.cloud.websocket.mq.tag}")
   private String tag;
   private final RocketMQTemplate template;

   public RocketmqMessageDistributor(WebSocketSessionStore webSocketSessionStore, RocketMQTemplate template) {
      super(webSocketSessionStore);
      this.template = template;
   }

   public void distribute(MessageDO messageDO) {
      LogUtils.info("the send message body is [{}]", new Object[]{messageDO});
      String destination = this.appName + ":" + this.tag;
      SendResult sendResult = (SendResult)this.template.sendAndReceive(destination, JacksonUtils.toJson(messageDO), SendResult.class);
      if (LogUtils.isDebugEnabled()) {
         LogUtils.debug("send message to `{}` finished. result:{}", new Object[]{destination, sendResult});
      }

   }

   public void onMessage(MessageExt message) {
      String body = new String(message.getBody(), StandardCharsets.UTF_8);
      MessageDO event = (MessageDO)JacksonUtils.toObject(body, MessageDO.class);
      LogUtils.info("the content is [{}]", new Object[]{event});

      try {
         this.doSend(event);
      } catch (Exception e) {
         LogUtils.error("MQ\u6d88\u8d39\u4fe1\u606f\u5904\u7406\u5f02\u5e38: {}", new Object[]{e.getMessage(), e});
         throw new ErrorJsonMessageException("MQ\u6d88\u8d39\u4fe1\u606f\u5904\u7406\u5f02\u5e38, " + e.getMessage());
      }
   }
}
