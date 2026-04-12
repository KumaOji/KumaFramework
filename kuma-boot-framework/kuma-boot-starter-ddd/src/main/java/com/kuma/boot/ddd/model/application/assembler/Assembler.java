package com.kuma.boot.ddd.model.application.assembler;

@FunctionalInterface
public interface Assembler {
   void assemble(Object source, Object target);
}
