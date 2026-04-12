package com.kuma.boot.ddd.model.domain;

public interface ValueObject extends DomainModelValidate {
   boolean sameValueAs(Object other);
}
