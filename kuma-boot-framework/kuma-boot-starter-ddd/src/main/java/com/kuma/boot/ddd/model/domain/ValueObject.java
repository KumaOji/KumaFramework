//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.domain;

public interface ValueObject<T> extends DomainModelValidate {
   boolean sameValueAs(T other);
}
