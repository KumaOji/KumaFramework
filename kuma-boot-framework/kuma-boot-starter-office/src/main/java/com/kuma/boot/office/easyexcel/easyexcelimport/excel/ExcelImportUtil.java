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

package com.kuma.boot.office.easyexcel.easyexcelimport.excel;

import com.alibaba.excel.EasyExcel;
import com.kuma.boot.office.easyexcel.easyexcelimport.refactor.ThrowingConsumer;

import java.io.InputStream;
import java.util.List;

/** 导入excel模板 */
public class ExcelImportUtil<T> {

    /**
     * 通用导入excel文件方法
     *
     * @param fileStream 导入的文件流
     * @param rowDto 接收excel每行数据的实体
     * @param rowAction 将接收到的实体进行自定义的业务处理逻辑方法
     * @param <T> 实体类型
     */
    public static <T> void importFile(InputStream fileStream, T rowDto, ThrowingConsumer<List<T>> rowAction) {
        // 获取excel通用监听器
        com.kuma.boot.office.easyexcel.easyexcelimport.excel.ExcelImportCommonListener<T> commonListener = new com.kuma.boot.office.easyexcel.easyexcelimport.excel.ExcelImportCommonListener<>(rowAction);
        // 读取excel文件并导入
        EasyExcel.read(fileStream, rowDto.getClass(), commonListener).sheet().doRead();
    }
}
