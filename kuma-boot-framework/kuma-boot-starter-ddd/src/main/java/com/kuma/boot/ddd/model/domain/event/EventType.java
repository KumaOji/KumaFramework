package com.kuma.boot.ddd.model.domain.event;

public enum EventType {
   LOGIN,
   CAPTCHA,
   API;

   // $FF: synthetic method
   private static EventType[] $values() {
      return new EventType[]{LOGIN, CAPTCHA, API};
   }
}
