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

package com.kuma.boot.office.easyexcel.easyexcelconvert.convert;

import cn.hutool.core.annotation.AnnotationUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.kuma.boot.office.easyexcel.easyexcelconvert.EasyExcelUtils;

import java.lang.reflect.Field;

import com.kuma.boot.office.easyexcel.easyexcelconvert.annotation.ExcelDictFormat;
import com.kuma.boot.common.utils.lang.StringUtils;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;

/**
 * 字典格式化转换处理
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-31 20:54:03
 */
public class ExcelDictConvert implements Converter<Object> {

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
        String type = anno.dictType();
        String label = cellData.getStringValue();
        String value;
        if (StringUtils.isBlank(type)) {
            value = EasyExcelUtils.reverseByExp(label, anno.readConverterExp(), anno.separator());
        } else {
            value = "";
            // value = SpringUtils.getBean(DictService.class).getDictValue(type, label,
            // anno.separator());
        }
        return Convert.convert(contentProperty.getField().getType(), value);
    }

    @Override
    public WriteCellData<String> convertToExcelData(
            Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjUtil.isNull(object)) {
            return new WriteCellData<>("");
        }
        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
        String type = anno.dictType();
        String value = Convert.toStr(object);
        String label;
        if (StringUtils.isBlank(type)) {
            label = EasyExcelUtils.convertByExp(value, anno.readConverterExp(), anno.separator());
        } else {
            label = "";
            // label = SpringUtils.getBean(DictService.class).getDictLabel(type, value,
            // anno.separator());
        }
        return new WriteCellData<>(label);
    }

    private ExcelDictFormat getAnnotation(Field field) {
        return AnnotationUtil.getAnnotation(field, ExcelDictFormat.class);
    }
}
