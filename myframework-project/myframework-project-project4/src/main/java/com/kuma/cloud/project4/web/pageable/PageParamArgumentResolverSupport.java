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

package com.kuma.cloud.project4.web.pageable;

import com.kuma.boot.common.constant.PageableConstants;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.model.request.PageParam;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

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
        if (sorts.isEmpty()) {
            sorts = getSortListFromMapStyle(request);
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

    /**
     * 解析 sorts[field]=id&sorts[asc]=true 或 sorts[0].field=id&sorts[0].asc=true 或 sorts[0][field]=id 格式
     * 支持多字段：sorts[0][field]=id&sorts[0][asc]=false&sorts[1][field]=name&sorts[1][asc]=true
     */
    protected List<PageParam.Sort> getSortListFromMapStyle(HttpServletRequest request) {
        List<PageParam.Sort> sorts = new ArrayList<>();
        java.util.Map<String, String[]> paramMap = request.getParameterMap();
        // 支持 sorts[0][field], sorts[1][field] 等多组
        for (int i = 0; i < 16; i++) {
            String field = i == 0
                    ? getParam(request, "sorts[field]", "sorts[0].field", "sorts[0][field]")
                    : getParam(request, "sorts[" + i + "].field", "sorts[" + i + "][field]");
            if (field == null && i == 0) {
                field = findParamBySuffix(paramMap, "field");
            }
            if (field == null && i > 0) {
                field = findParamByIndex(paramMap, i, "field");
            }
            if (!org.springframework.util.StringUtils.hasText(field)) {
                break;
            }
            String ascStr = i == 0
                    ? getParam(request, "sorts[asc]", "sorts[0].asc", "sorts[0][asc]")
                    : getParam(request, "sorts[" + i + "].asc", "sorts[" + i + "][asc]");
            if (ascStr == null && i == 0) {
                ascStr = findParamBySuffix(paramMap, "asc");
            }
            if (ascStr == null && i > 0) {
                ascStr = findParamByIndex(paramMap, i, "asc");
            }
            String order = Boolean.parseBoolean(ascStr) ? PageableConstants.ASC : "desc";
            fillValidSort(field.trim(), order, sorts);
        }
        return sorts;
    }

    private static String findParamByIndex(java.util.Map<String, String[]> paramMap, int index, String suffix) {
        if (paramMap == null) return null;
        String keyPattern = "sorts[" + index + "]";
        for (java.util.Map.Entry<String, String[]> e : paramMap.entrySet()) {
            String key = e.getKey();
            if (key != null && key.contains(keyPattern) && key.contains(suffix)) {
                String[] v = e.getValue();
                if (v != null && v.length > 0 && org.springframework.util.StringUtils.hasText(v[0])) {
                    return v[0];
                }
            }
        }
        return null;
    }

    private static String getParam(HttpServletRequest request, String... names) {
        for (String name : names) {
            String value = request.getParameter(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static String findParamBySuffix(java.util.Map<String, String[]> paramMap, String suffix) {
        if (paramMap == null) return null;
        for (java.util.Map.Entry<String, String[]> e : paramMap.entrySet()) {
            String key = e.getKey();
            if (key != null && key.contains("sorts") && key.contains(suffix)) {
                String[] v = e.getValue();
                if (v != null && v.length > 0 && org.springframework.util.StringUtils.hasText(v[0])) {
                    return v[0];
                }
            }
        }
        return null;
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
        String[] fieldArr = sortFields.split(SymbolConstants.COMMA);
        String[] orderArr = sortOrders.split(SymbolConstants.COMMA);
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
            long size = pageParam.getSize();
            if (size > this.maxPageSize) {
                binder.getBindingResult().addError(new ObjectError("pageParam", "分页条数不能大于" + this.maxPageSize));
                if (isBindExceptionRequired(binder, parameter)) {
                    throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
                }
            }
            if (mavContainer != null) {
                mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + "pageParam", binder.getBindingResult());
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
