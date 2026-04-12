/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.webagg.controller;

import com.kuma.boot.common.model.result.EmptyResult;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.support.utils.SecurityUtils;

/**
 * BaseController
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 21:03:10
 */
public interface CommonController {

   /**
    * 成功返回
    *
    * @param data 返回内容
    * @param <R>  返回类型
    * @return 成功数据
    * @since 2021-09-02 21:03:37
    */
   default <R> Result<R> success(R data) {
      return Result.success(data);
   }

   /**
    * 成功返回
    *
    * @return 成功数据
    * @since 2021-09-02 21:03:51
    */
   default Result<Boolean> success() {
      return Result.success(true);
   }

   /**
    * 失败返回
    *
    * @param msg 失败消息
    * @return 失败数据
    * @since 2021-09-02 21:04:00
    */
   default Result<EmptyResult> fail(String msg) {
      return Result.fail(msg);
   }

   ///**
   // * 失败返回
   // *
   // * @param code 失败编码
   // * @param msg  失败消息
   // * @return 失败数据
   // * @since 2021-09-02 21:04:08
   // */
   //default Result<String> fail(int code, String msg) {
   //    return Result.fail(msg, code);
   //}
   //
   ///**
   // * 失败返回
   // *
   // * @param exception 异常
   // * @return 失败数据
   // * @since 2021-09-02 21:04:19
   // */
   //default <R> Result<R> fail(BusinessException exception) {
   //    return Result.fail(exception);
   //}
   //
   ///**
   // * 失败返回
   // *
   // * @param throwable 异常
   // * @return 失败数据
   // * @since 2021-09-02 21:04:28
   // */
   //default <R> Result<R> fail(Throwable throwable) {
   //    return Result.fail(throwable);
   //}
   //
   ///**
   // * 参数校验失败返回
   // *
   // * @param msg 错误消息
   // * @return 失败数据
   // * @since 2021-09-02 21:04:35
   // */
   //default <R> Result<R> validFail(String msg) {
   //    return Result.validFail(msg);
   //}
   //
   ///**
   // * 参数校验失败返回
   // *
   // * @param msg  错误消息
   // * @param args 错误参数
   // * @return 失败数据
   // * @since 2021-09-02 21:04:42
   // */
   //default <R> Result<R> validFail(String msg, Object... args) {
   //    return Result.validFail(msg, args);
   //}
   //
   ///**
   // * 参数校验失败返回
   // *
   // * @param resultEnum 错误编码
   // * @return 失败数据
   // * @since 2021-09-02 21:04:50
   // */
   //default <R> Result<R> validFail(ResultEnum resultEnum) {
   //    return Result.validFail(resultEnum);
   //}

   /**
    * 获取当前id
    *
    * @return 用户id
    * @since 2021-09-02 21:04:56
    */
   default Long getUserId() {
      return SecurityUtils.getUserId();
   }

   /**
    * 当前请求租户
    *
    * @return 租户id
    * @since 2021-09-02 21:05:02
    */
   default String getTenant() {
      return SecurityUtils.getTenant();
   }

   /**
    * 登录人账号
    *
    * @return 登录人账号
    * @since 2021-09-02 21:05:12
    */
   default String getNickname() {
      return SecurityUtils.getCurrentUser().getNickname();
   }

   /**
    * 登录人姓名
    *
    * @return 登录人姓名
    * @since 2021-09-02 21:05:17
    */
   default String getUsername() {
      return SecurityUtils.getUsername();
   }
}
