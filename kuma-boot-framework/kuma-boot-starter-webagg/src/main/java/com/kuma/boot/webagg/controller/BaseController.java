package com.kuma.boot.webagg.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.EmptyResult;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.support.utils.SecurityUtils;
import com.kuma.boot.webagg.entity.SuperEntity;
import com.kuma.boot.webagg.service.BaseSuperService;
import java.io.Serializable;

public interface BaseController<T extends SuperEntity<T, I>, I extends Serializable> {
   Class<T> getEntityClass();

   BaseSuperService<T, I> service();

   default <R> Result<R> success(R data) {
      return Result.success(data);
   }

   default Result<Boolean> success() {
      return Result.success(true);
   }

   default Result<EmptyResult> fail(String msg) {
      return Result.fail(msg);
   }

   default Result<EmptyResult> fail(BusinessException exception) {
      return Result.fail(exception);
   }

   default Result<EmptyResult> fail(Throwable throwable) {
      return Result.fail(throwable);
   }

   default Long getUserId() {
      return SecurityUtils.getUserId();
   }

   default String getTenant() {
      return SecurityUtils.getTenant();
   }

   default String getNickname() {
      return SecurityUtils.getCurrentUser().getNickname();
   }

   default String getUsername() {
      return SecurityUtils.getUsername();
   }
}
