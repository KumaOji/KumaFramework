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
      LogUtils.info("\u6536\u5230\u6d88\u606f \u8be5\u6d88\u606f\u63a5\u6536\u5230\u540e\uff0c\u53ef\u4ee5\u63a8\u9001\u90ae\u7bb1\u6216\u8005\u77ed\u4fe1\uff1a" + String.valueOf(skywalkingAlarmMessageList), new Object[0]);
      this.messageList.addAll(skywalkingAlarmMessageList);
   }

   @GetMapping({"list_alarm_msg"})
   public List<SkywalkingAlarmMessage> list() {
      return this.messageList;
   }
}
