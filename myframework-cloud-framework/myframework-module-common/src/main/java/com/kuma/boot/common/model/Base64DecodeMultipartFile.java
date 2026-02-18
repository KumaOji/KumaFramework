/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.model;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Base64.Decoder;
import org.springframework.web.multipart.MultipartFile;

/**
 * base64转为multipartFile工具类
 *
 * MultipartFile multipartFile = BASE64DecodedMultipartFile.base64ToMultipart(base64);
 * multipartFile.getInputStream();
 *
 * @author kuma
 * @version 2023.01
 * @since 2023-01-03 11:33:41
 */
public class Base64DecodeMultipartFile implements MultipartFile {

    /** img内容 */
    private final byte[] imgContent;

    /** 头 */
    private final String header;

    /**
     * base64解码多部分文件
     * @param imgContent img内容
     * @param header 头
     * @since 2023-01-03 11:33:41
     */
    public Base64DecodeMultipartFile(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        this.header = header.split(";")[0];
    }

    /**
     * 得到名字
     * @return {@link String }
     * @since 2023-01-03 11:33:41
     */
    @Override
    public String getName() {
        return System.currentTimeMillis() + Math.random() + "." + header.split("/")[1];
    }

    /**
     * 得到原始文件名
     * @return {@link String }
     * @since 2023-01-03 11:33:42
     */
    @Override
    public String getOriginalFilename() {
        return System.currentTimeMillis()
                + (int) Math.random() * 10000
                + "."
                + header.split("/")[1];
    }

    /**
     * 让内容类型
     * @return {@link String }
     * @since 2023-01-03 11:33:42
     */
    @Override
    public String getContentType() {
        return header.split(":")[1];
    }

    /**
     * 是空
     * @return boolean
     * @since 2023-01-03 11:33:42
     */
    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }

    /**
     * 得到大小
     * @return long
     * @since 2023-01-03 11:33:42
     */
    @Override
    public long getSize() {
        return imgContent.length;
    }

    /**
     * 得到字节
     * @return {@link byte[] }
     * @since 2023-01-03 11:33:42
     */
    @Override
    public byte[] getBytes() throws IOException {
        return imgContent;
    }

    /**
     * 获取输入流
     * @return {@link InputStream }
     * @since 2023-01-03 11:33:42
     */
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(imgContent);
    }

    /**
     * 转移到
     * @param dest 桌子
     * @since 2023-01-03 11:33:42
     */
    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(dest);
            stream.write(imgContent);
        } catch (IOException e) {
            LogUtils.error("transferTo错误", e);
        } finally {
            assert stream != null;
            stream.close();
        }
    }

    /**
     * base64转换
     * @param base64 base64
     * @return {@link MultipartFile }
     * @since 2023-01-03 11:33:42
     */
    public static MultipartFile base64Convert(String base64) {

        String[] baseStrs = base64.split(",");
        Decoder decoder = Base64.getDecoder();
        byte[] b = decoder.decode(baseStrs[1]);

        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                b[i] += 256;
            }
        }
        return new Base64DecodeMultipartFile(b, baseStrs[0]);
    }

    /**
     * base64输入流
     * @param base64 base64
     * @return {@link InputStream }
     * @since 2023-01-03 11:33:42
     */
    public static InputStream base64ToInputStream(String base64) {
        ByteArrayInputStream stream = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            stream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            LogUtils.error("base64ToInputStream错误", e);
        }
        return stream;
    }

    /**
     * 输入流,流
     * @param in 在
     * @return {@link String }
     * @since 2023-01-03 11:33:43
     */
    public static String inputStreamToStream(InputStream in) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            LogUtils.error("转码错误", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtils.error("inputStreamToStream错误", e);
                }
            }
        }
        return Base64.getEncoder().encodeToString(data);
    }
}
