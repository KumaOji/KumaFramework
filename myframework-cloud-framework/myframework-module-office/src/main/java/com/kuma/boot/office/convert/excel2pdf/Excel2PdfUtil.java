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

package com.kuma.boot.office.convert.excel2pdf;

import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Excel 转 Pdf 工具类
 *
 * @since 2020/11/24 11:23
 */
public class Excel2PdfUtil {

    /**
     * `excel` 转 `pdf`
     *
     * @param excelBytes: html字节码
     * @return 生成的`pdf`文件流
     * @since 2020/11/24 11:26
     */
    public static byte[] excelBytes2PdfBytes(byte[] excelBytes) throws Exception {
        Workbook workbook = new Workbook(new ByteArrayInputStream(excelBytes));
        // 设置pdf保存的格式以及强制所有列都在同一页
        PdfSaveOptions pso = new PdfSaveOptions();
        pso.setAllColumnsInOnePagePerSheet(true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.save(outputStream, pso);
        // workbook.save(outputStream, SaveFormat.PDF);
        // 返回生成的`pdf`文件字节码
        return outputStream.toByteArray();
    }

    /**
     * `excel` 转 `pdf`
     *
     * @param excelBytes: html字节码
     * @param pdfFilePath: 待生成的`pdf`文件路径
     * @return 生成的`pdf`文件数据
     * @since 2020/11/24 11:26
     */
    public static File excelBytes2PdfFile(byte[] excelBytes, String pdfFilePath) throws Exception {
        Workbook workbook = new Workbook(new ByteArrayInputStream(excelBytes));
        // 设置pdf保存的格式以及强制所有列都在同一页
        PdfSaveOptions pso = new PdfSaveOptions();
        pso.setAllColumnsInOnePagePerSheet(true);
        workbook.save(pdfFilePath, pso);
        // workbook.save(outputStream, SaveFormat.PDF);
        return new File(pdfFilePath);
    }
}
