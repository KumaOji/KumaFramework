package com.kuma.boot.ddd.model.types;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface MarkerCheck extends MarkerInterface {
   default void check() throws MethodArgumentNotValidException, ConstraintViolationException {
   }
}
