/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.web.gracefulresponse;

import com.kuma.boot.web.gracefulresponse.defaults.DefaultConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * 核心配置类.
 */
@ConfigurationProperties(prefix = "kuma.boot.web.graceful-response")
public class GracefulResponseProperties {

    /**
     * 在全局异常处理器中打印异常，默认不打印
     */
    private boolean printExceptionInGlobalAdvice = false;

    /**
     * 默认的Response实现类名称，配置了responseClassFullName，则responseStyle不生效
     */
    private String responseClassFullName;

    /**
     * responseStyle的风格，responseClassFullName为空时才会生效
     * responseStyle==null或者responseStyle==0,Response风格为 DefaultResponseImplStyle0
     * responseStyle=1,Response风格为 DefaultResponseImplStyle1
     */
    private Integer responseStyle;

    /**
     * 默认的成功返回码
     */
    private String defaultSuccessCode = DefaultConstants.DEFAULT_SUCCESS_CODE;

    /**
     * 默认的成功提示
     */
    private String defaultSuccessMsg = DefaultConstants.DEFAULT_SUCCESS_MSG;

    /**
     * 默认的失败码
     */
    private String defaultErrorCode = DefaultConstants.DEFAULT_ERROR_CODE;

    /**
     * 默认的失败提示
     */
    private String defaultErrorMsg = DefaultConstants.DEFAULT_ERROR_MSG;

    /**
     * Validate异常码，不提供的话默认DefaultConstants.DEFAULT_ERROR_CODE
     */
    private String defaultValidateErrorCode = DefaultConstants.DEFAULT_ERROR_CODE;

    /**
     * 例外包路径，这些包下的 Controller 返回值不再被 GracefulResponse 包装。
     * 默认排除 org.springdoc，保证 /v3/api-docs、/swagger-ui 等返回原始 OpenAPI JSON。
     */
    private List<String> excludePackages = Arrays.asList("org.springdoc*");

    /**
     * 例外路径，这些路径下的请求发生异常时不包装为 GracefulResponse，直接抛出原始异常。
     * 默认排除 /v3/api-docs/**、/swagger-ui/**，便于 Swagger/OpenAPI 相关错误直接暴露。
     */
    private List<String> excludePaths = Arrays.asList("/v3/api-docs/**", "/swagger-ui/**", "/doc.html");

    /**
     * 不使用@ExceptionMapper和@ExceptionAliasFor修饰的原生异常
     * 是否使用异常信息Throwable类的detailMessage进行返回
     * originExceptionUsingDetailMessage=false，则msg=defaultErrorMsg
     */
    private Boolean originExceptionUsingDetailMessage = false;

    public boolean isPrintExceptionInGlobalAdvice() {
        return printExceptionInGlobalAdvice;
    }

    public void setPrintExceptionInGlobalAdvice(boolean printExceptionInGlobalAdvice) {
        this.printExceptionInGlobalAdvice = printExceptionInGlobalAdvice;
    }

    public String getDefaultSuccessCode() {
        return defaultSuccessCode;
    }

    public void setDefaultSuccessCode(String defaultSuccessCode) {
        this.defaultSuccessCode = defaultSuccessCode;
    }

    public String getDefaultSuccessMsg() {
        return defaultSuccessMsg;
    }

    public void setDefaultSuccessMsg(String defaultSuccessMsg) {
        this.defaultSuccessMsg = defaultSuccessMsg;
    }

    public String getDefaultErrorCode() {
        return defaultErrorCode;
    }

    public void setDefaultErrorCode(String defaultErrorCode) {
        this.defaultErrorCode = defaultErrorCode;
    }

    public String getDefaultErrorMsg() {
        return defaultErrorMsg;
    }

    public void setDefaultErrorMsg(String defaultErrorMsg) {
        this.defaultErrorMsg = defaultErrorMsg;
    }

    public String getResponseClassFullName() {
        return responseClassFullName;
    }

    public void setResponseClassFullName(String responseClassFullName) {
        this.responseClassFullName = responseClassFullName;
    }

    public Integer getResponseStyle() {
        return responseStyle;
    }

    public void setResponseStyle(Integer responseStyle) {
        this.responseStyle = responseStyle;
    }

    public String getDefaultValidateErrorCode() {
        return defaultValidateErrorCode;
    }

    public void setDefaultValidateErrorCode(String defaultValidateErrorCode) {
        this.defaultValidateErrorCode = defaultValidateErrorCode;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(List<String> excludePackages) {
        this.excludePackages = excludePackages;
    }

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    public Boolean getOriginExceptionUsingDetailMessage() {
        return originExceptionUsingDetailMessage;
    }

    public void setOriginExceptionUsingDetailMessage(Boolean originExceptionUsingDetailMessage) {
        this.originExceptionUsingDetailMessage = originExceptionUsingDetailMessage;
    }
}
