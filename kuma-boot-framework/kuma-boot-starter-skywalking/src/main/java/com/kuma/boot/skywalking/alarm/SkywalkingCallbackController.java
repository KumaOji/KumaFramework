package com.kuma.boot.skywalking.alarm;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/skywalking")
public class SkywalkingCallbackController {
   private List<SkywalkingAlarmMessage> messageList = new ArrayList();

   public SkywalkingCallbackController() {
   }

   @PostMapping({"/webhook"})
   public void webhook(@RequestBody List<SkywalkingAlarmMessage> skywalkingAlarmMessageList) {
      LogUtils.info("收到消息 该消息接收到后，可以推送邮箱或者短信：" + String.valueOf(skywalkingAlarmMessageList), new Object[0]);
      this.messageList.addAll(skywalkingAlarmMessageList);
   }

   @GetMapping({"list_alarm_msg"})
   public List<SkywalkingAlarmMessage> list() {
      return this.messageList;
   }
}
