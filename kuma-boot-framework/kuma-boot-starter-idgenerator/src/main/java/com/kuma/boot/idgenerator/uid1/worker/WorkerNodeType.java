package com.kuma.boot.idgenerator.uid1.worker;

import com.kuma.boot.idgenerator.uid1.utils.ValuedEnum;

public enum WorkerNodeType implements ValuedEnum<Integer> {
   CONTAINER(1),
   ACTUAL(2);

   private final Integer type;

   private WorkerNodeType(Integer type) {
      this.type = type;
   }

   public Integer value() {
      return this.type;
   }

   // $FF: synthetic method
   private static WorkerNodeType[] $values() {
      return new WorkerNodeType[]{CONTAINER, ACTUAL};
   }
}
