//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.application.assembler;

import org.springframework.beans.BeanUtils;

public class AssemblerFactory {
   public AssemblerFactory() {
   }

   public <S, T> void assemble(Assembler<S, T> assembler, S source, T target) {
      assembler.assemble(source, target);
   }

   public <S, T> T convert(Assembler<S, T> assembler, S source, Class<T> type) {
      T target = (T)BeanUtils.instantiateClass(type);
      this.assemble(assembler, source, target);
      return target;
   }
}
