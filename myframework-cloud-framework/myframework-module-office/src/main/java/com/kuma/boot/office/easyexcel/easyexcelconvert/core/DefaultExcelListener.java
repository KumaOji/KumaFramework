/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.stream.StreamUtil
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.excel.context.AnalysisContext
 *  com.alibaba.excel.event.AnalysisEventListener
 *  com.alibaba.excel.exception.ExcelAnalysisException
 *  com.alibaba.excel.exception.ExcelDataConvertException
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.validator.ValidatorUtils
 *  jakarta.validation.ConstraintViolation
 *  jakarta.validation.ConstraintViolationException
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.core;

import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.validator.ValidatorUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Set;

public class DefaultExcelListener<T>
extends AnalysisEventListener<T>
implements ExcelListener<T> {
    private final Boolean isValidate;
    private Map<Integer, String> headMap;
    private final ExcelResult<T> excelResult = new DefautExcelResult();

    public DefaultExcelListener(boolean isValidate) {
        this.isValidate = isValidate;
    }

    public DefaultExcelListener() {
        this.isValidate = true;
    }

    public void onException(Exception exception, AnalysisContext context) throws Exception {
        String errMsg = null;
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            Integer rowIndex = excelDataConvertException.getRowIndex();
            Integer columnIndex = excelDataConvertException.getColumnIndex();
            errMsg = StrUtil.format((CharSequence)"\u7b2c{}\u884c-\u7b2c{}\u5217-\u8868\u5934{}: \u89e3\u6790\u5f02\u5e38<br/>", (Object[])new Object[]{rowIndex + 1, columnIndex + 1, this.headMap.get(columnIndex)});
            if (LogUtils.isDebugEnabled()) {
                LogUtils.error((String)errMsg, (Object[])new Object[0]);
            }
        }
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException)exception;
            Set constraintViolations = constraintViolationException.getConstraintViolations();
            String constraintViolationsMsg = StreamUtil.join(constraintViolations.stream(), (CharSequence)", ", ConstraintViolation::getMessage);
            errMsg = StrUtil.format((CharSequence)"\u7b2c{}\u884c\u6570\u636e\u6821\u9a8c\u5f02\u5e38: {}", (Object[])new Object[]{context.readRowHolder().getRowIndex() + 1, constraintViolationsMsg});
            if (LogUtils.isDebugEnabled()) {
                LogUtils.error((String)errMsg, (Object[])new Object[0]);
            }
        }
        this.excelResult.getErrorList().add(errMsg);
        throw new ExcelAnalysisException(errMsg);
    }

    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
        LogUtils.debug((String)"\u89e3\u6790\u5230\u4e00\u6761\u8868\u5934\u6570\u636e: {}", (Object[])new Object[]{JacksonUtils.toJSONString(headMap)});
    }

    public void invoke(T data, AnalysisContext context) {
        if (this.isValidate.booleanValue()) {
            ValidatorUtils.validate(data, (Class[])new Class[0]);
        }
        this.excelResult.getList().add(data);
    }

    public void doAfterAllAnalysed(AnalysisContext context) {
        LogUtils.debug((String)"\u6240\u6709\u6570\u636e\u89e3\u6790\u5b8c\u6210\uff01", (Object[])new Object[0]);
    }

    @Override
    public ExcelResult<T> getExcelResult() {
        return this.excelResult;
    }
}

