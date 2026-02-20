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

package com.kuma.boot.office.convert.doc2docx;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Doc 转 Docx 工具类
 *
 * @since 2020/11/24 11:23
 */
public class Doc2DocxUtil {

    /**
     * `doc` 转 `docx`
     *
     * @param docBytes: doc文件字节码
     * @return 生成的`docx`文件字节码
     * @since 2020/11/24 11:26
     */
    public static byte[] docBytes2DocxBytes(byte[] docBytes) throws Exception {
        Document document = new Document(new ByteArrayInputStream(docBytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream, SaveFormat.DOCX);
        return outputStream.toByteArray();
    }

    /**
     * `doc` 转 `docx`
     *
     * @param docBytes: doc文件字节码
     * @param docxFilePath: docx文件路径
     * @return 生成的`docx`文件数据
     * @since 2020/11/24 11:26
     */
    public static File docBytes2DocxFile(byte[] docBytes, String docxFilePath) throws Exception {
        Document document = new Document(new ByteArrayInputStream(docBytes));
        document.save(docxFilePath, SaveFormat.DOCX);
        return new File(docxFilePath);
    }

    /**
     * `doc` 转 `docx`
     *
     * @param fileRootPath: 文件根位置
     * @param wordFileName: 需转换的`doc`文件名
     * @param wordFileNameNew: 待生成的的`docx`文件名
     * @return 生成的`docx`文件路径
     * @since 2020/11/24 11:26
     */
    public static String docBytes2DocxFile(String fileRootPath, String wordFileName, String wordFileNameNew)
            throws Exception {
        // word 文件路径
        final String wordFilePath = fileRootPath + "/" + wordFileName;
        final String wordFilePathNew = fileRootPath + "/" + wordFileNameNew;
        // 将`doc`格式转换成`docx`
        Document document = new Document(wordFilePath);
        document.save(wordFilePathNew);
        // 返回生成的`docx`文件路径
        return wordFilePathNew;
    }
}
