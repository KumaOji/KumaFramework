package com.kuma.boot.eventbus.disruptor.tmp5.event;

import java.time.LocalDateTime;
import java.util.List;

public class DisruptorEvent {
   private LocalDateTime ldt = LocalDateTime.now();
   private Object o1;
   private Object o2;
   private Object o3;
   private List<Object> objectList;

   public DisruptorEvent() {
   }

   public void clear() {
      this.o1 = null;
      this.o2 = null;
      this.o3 = null;
      this.objectList = null;
   }

   public LocalDateTime getLdt() {
      return this.ldt;
   }

   public void setLdt(LocalDateTime ldt) {
      this.ldt = ldt;
   }

   public Object getO1() {
      return this.o1;
   }

   public void setO1(Object o1) {
      this.o1 = o1;
   }

   public Object getO2() {
      return this.o2;
   }

   public void setO2(Object o2) {
      this.o2 = o2;
   }

   public Object getO3() {
      return this.o3;
   }

   public void setO3(Object o3) {
      this.o3 = o3;
   }

   public List<Object> getObjectList() {
      return this.objectList;
   }

   public void setObjectList(List<Object> objectList) {
      this.objectList = objectList;
   }
}
