package com.kuma.boot.ddd.model.application.assembler;

import org.springframework.beans.BeanUtils;

public class AssemblerFactory {
   public void assemble(Assembler assembler, Object source, Object target) {
      assembler.assemble(source, target);
   }

   public Object convert(Assembler assembler, Object source, Class type) {
      T target = (T)BeanUtils.instantiateClass(type);
      this.assemble(assembler, source, target);
      return target;
   }
}
