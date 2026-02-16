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

package com.kuma.boot.spring.web.pageable;

import com.kuma.boot.common.core.constant.Symbol;
import com.kuma.boot.common.model.domain.PageParam;
import com.kuma.boot.common.model.domain.PageableConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.ValidationAnnotationUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页参数解析器支持类
 *
 * @author kuma
 */
@Slf4j
public abstract class PageParamArgumentResolverSupport {

    @Setter
    @Getter
    private String pageParameterName = PageableConstants.DEFAULT_PAGE_PARAMETER;

    @Setter
    @Getter
    private String sizeParameterName = PageableConstants.DEFAULT_SIZE_PARAMETER;

    @Setter
    @Getter
    private String sortParameterName = PageableConstants.DEFAULT_SORT_PARAMETER;

    @Setter
    @Getter
    private int maxPageSize = PageableConstants.DEFAULT_MAX_PAGE_SIZE;

    protected PageParam getPageParam(MethodParameter parameter, HttpServletRequest request) {
        String pageParameterValue = request.getParameter(this.pageParameterName);
        String sizeParameterValue = request.getParameter(this.sizeParameterName);

        PageParam pageParam;
        try {
            pageParam = (PageParam) parameter.getParameterType().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            pageParam = new PageParam();
        }

        long pageValue = parseValueFormString(pageParameterValue, 1);
        pageParam.setPage(pageValue);
        pageParam.setCurrent(pageValue);

        long sizeValue = parseValueFormString(sizeParameterValue, 10);
        pageParam.setSize(sizeValue);

        String[] sort = request.getParameterMap().get(this.sortParameterName);
        if (ObjectUtils.isEmpty(sort)) {
            sort = request.getParameterMap().get(this.sortParameterName + "[]");
        }

        List<PageParam.Sort> sorts;
        if (!ObjectUtils.isEmpty(sort)) {
            sorts = getSortList(sort);
        } else {
            String sortFields = request.getParameter(PageableConstants.SORT_FIELDS);
            String sortOrders = request.getParameter(PageableConstants.SORT_ORDERS);
            sorts = getSortList(sortFields, sortOrders);
        }
        pageParam.setSorts(sorts);

        return pageParam;
    }

    private long parseValueFormString(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    protected List<PageParam.Sort> getSortList(String[] sort) {
        List<PageParam.Sort> sorts = new ArrayList<>();
        for (String sortRule : sort) {
            if (sortRule == null) continue;
            String[] sortRuleArr = sortRule.split(",");
            String field = sortRuleArr[0];
            String order = sortRuleArr.length < 2 ? PageableConstants.ASC : sortRuleArr[1];
            fillValidSort(field, order, sorts);
        }
        return sorts;
    }

    @Deprecated
    protected List<PageParam.Sort> getSortList(String sortFields, String sortOrders) {
        List<PageParam.Sort> sorts = new ArrayList<>();
        if (!org.springframework.util.StringUtils.hasText(sortFields) || !org.springframework.util.StringUtils.hasText(sortOrders)) {
            return sorts;
        }
        String[] fieldArr = sortFields.split(Symbol.COMMA);
        String[] orderArr = sortOrders.split(Symbol.COMMA);
        if (fieldArr.length != orderArr.length) {
            return sorts;
        }
        for (int i = 0; i < fieldArr.length; i++) {
            fillValidSort(fieldArr[i], orderArr[i], sorts);
        }
        return sorts;
    }

    protected void fillValidSort(String field, String order, List<PageParam.Sort> sorts) {
        if (validFieldName(field)) {
            PageParam.Sort sort = new PageParam.Sort();
            sort.setAsc(PageableConstants.ASC.equalsIgnoreCase(order));
            sort.setField(StringUtils.toUnderScoreCase(field));
            sorts.add(sort);
        }
    }



    protected boolean validFieldName(String filedName) {
        boolean isValid = org.springframework.util.StringUtils.hasText(filedName)
                && filedName.matches(PageableConstants.SORT_FILED_REGEX)
                && !PageableConstants.SQL_KEYWORDS.contains(filedName);
        if (!isValid) {
            log.warn("异常的分页查询排序字段：{}", filedName);
        }
        return isValid;
    }

    protected void paramValidate(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory, PageParam pageParam) throws Exception {
        if (binderFactory != null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, pageParam, "pageParam");
            validateIfApplicable(binder, parameter);
            BindingResult bindingResult = binder.getBindingResult();

            long size = pageParam.getSize();
            if (size > this.maxPageSize) {
                bindingResult.addError(new ObjectError("size", "分页条数不能大于" + this.maxPageSize));
            }

            if (bindingResult.hasErrors() && isBindExceptionRequired(binder, parameter)) {
                throw new MethodArgumentNotValidException(parameter, bindingResult);
            }
            if (mavContainer != null) {
                mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + "pageParam", bindingResult);
            }
        }
    }

    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        for (Annotation ann : parameter.getParameterAnnotations()) {
            Object[] validationHints = ValidationAnnotationUtils.determineValidationHints(ann);
            if (validationHints != null) {
                binder.validate(validationHints);
                break;
            }
        }
    }

    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
        boolean hasBindingResult = (paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]));
        return !hasBindingResult;
    }
}
