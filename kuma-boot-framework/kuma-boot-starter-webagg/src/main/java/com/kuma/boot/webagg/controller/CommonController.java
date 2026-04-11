package com.kuma.boot.webagg.controller;

import com.kuma.boot.common.model.result.EmptyResult;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.support.utils.SecurityUtils;

public interface CommonController {
   default <R> Result<R> success(R data) {
      return Result.success(data);
   }

   default Result<Boolean> success() {
      return Result.success(true);
   }

   default Result<EmptyResult> fail(String msg) {
      return Result.fail(msg);
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
