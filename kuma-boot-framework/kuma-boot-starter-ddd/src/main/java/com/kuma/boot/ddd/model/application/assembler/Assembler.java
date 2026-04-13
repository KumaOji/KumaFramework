//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.application.assembler;

@FunctionalInterface
public interface Assembler<S, T> {
   void assemble(S source, T target);
}
