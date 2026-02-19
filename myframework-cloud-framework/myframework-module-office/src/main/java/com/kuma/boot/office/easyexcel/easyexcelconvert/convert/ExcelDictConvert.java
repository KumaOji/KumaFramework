/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.annotation.AnnotationUtil
 *  cn.hutool.core.convert.Convert
 *  cn.hutool.core.util.ObjUtil
 *  com.alibaba.excel.converters.Converter
 *  com.alibaba.excel.enums.CellDataTypeEnum
 *  com.alibaba.excel.metadata.GlobalConfiguration
 *  com.alibaba.excel.metadata.data.ReadCellData
 *  com.alibaba.excel.metadata.data.WriteCellData
 *  com.alibaba.excel.metadata.property.ExcelContentProperty
 *  com.kuma.boot.common.utils.lang.StringUtils
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.convert;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.office.easyexcel.easyexcelconvert.EasyExcelUtils;
import com.kuma.boot.office.easyexcel.easyexcelconvert.annotation.ExcelDictFormat;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

public class ExcelDictConvert
implements Converter<Object> {
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        ExcelDictFormat anno = this.getAnnotation(contentProperty.getField());
        String type = anno.dictType();
        String label = cellData.getStringValue();
        String value = StringUtils.isBlank((String)type) ? EasyExcelUtils.reverseByExp(label, anno.readConverterExp(), anno.separator()) : "";
        return Convert.convert(contentProperty.getField().getType(), (Object)value);
    }

    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjUtil.isNull((Object)object)) {
            return new WriteCellData("");
        }
        ExcelDictFormat anno = this.getAnnotation(contentProperty.getField());
        String type = anno.dictType();
        String value = Convert.toStr((Object)object);
        String label = StringUtils.isBlank((String)type) ? EasyExcelUtils.convertByExp(value, anno.readConverterExp(), anno.separator()) : "";
        return new WriteCellData(label);
    }

    private ExcelDictFormat getAnnotation(Field field) {
        return (ExcelDictFormat)AnnotationUtil.getAnnotation((AnnotatedElement)field, ExcelDictFormat.class);
    }
}

