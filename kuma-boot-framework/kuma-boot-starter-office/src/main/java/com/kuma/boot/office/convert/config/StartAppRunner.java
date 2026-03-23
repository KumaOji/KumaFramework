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

package com.kuma.boot.office.convert.config;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 服务初始化之后，执行方法
 *
 * @since 2020/5/22 19:29
 */
@Component
public class StartAppRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        LogUtils.info("《服务初始化执行处理》 start...");
        applyWordsLicense();
        applyCellsLicense();
        applyPdfLicense();
        applySlidesLicense();
        LogUtils.info("《服务初始化执行处理》 end...");
    }

    private void applyWordsLicense() {
        try {
//            InputStream is = new ClassPathResource("license.xml").getInputStream();
//            com.aspose.words.License license = new com.aspose.words.License();
//            license.setLicense(is);
            registerWord2412();
            LogUtils.info("aspose-words 授权成功");
        } catch (Exception e) {
            LogUtils.warn("aspose-words 授权失败（评估版水印保留）: {}", e.getMessage());
        }
    }

    private void applyCellsLicense() {
        try {
            InputStream is = new ClassPathResource("license.xml").getInputStream();
            com.aspose.cells.License license = new com.aspose.cells.License();
            license.setLicense(is);
            LogUtils.info("aspose-cells 授权成功");
        } catch (Exception e) {
            LogUtils.warn("aspose-cells 授权失败（评估版水印保留）: {}", e.getMessage());
        }
    }

    private void applyPdfLicense() {
        try {
            InputStream is = new ClassPathResource("license.xml").getInputStream();
            com.aspose.pdf.License license = new com.aspose.pdf.License();
            license.setLicense(is);
            LogUtils.info("aspose-pdf 授权成功");
        } catch (Exception e) {
            LogUtils.warn("aspose-pdf 授权失败（评估版水印保留）: {}", e.getMessage());
        }
    }

    private void applySlidesLicense() {
        try {
            InputStream is = new ClassPathResource("license.xml").getInputStream();
            com.aspose.slides.License license = new com.aspose.slides.License();
            license.setLicense(is);
            LogUtils.info("aspose-slides 授权成功");
        } catch (Exception e) {
            LogUtils.warn("aspose-slides 授权失败（评估版水印保留）: {}", e.getMessage());
        }
    }

    public static void registerWord2412() {
        try {
            Class<?> zzodClass = Class.forName("com.aspose.words.zzod");
            Constructor<?> constructors = zzodClass.getDeclaredConstructors()[0];
            constructors.setAccessible(true);
            Object instance = constructors.newInstance(null, null);
            Field zzWws = zzodClass.getDeclaredField("zzWws");
            zzWws.setAccessible(true);
            zzWws.set(instance, 1);
            Field zzVZC = zzodClass.getDeclaredField("zzVZC");
            zzVZC.setAccessible(true);
            zzVZC.set(instance, 1);

            Class<?> zz83Class = Class.forName("com.aspose.words.zz83");
            constructors.setAccessible(true);
            constructors.newInstance(null, null);

            Field zzZY4 = zz83Class.getDeclaredField("zzZY4");
            zzZY4.setAccessible(true);
            ArrayList<Object> zzwPValue = new ArrayList<>();
            zzwPValue.add(instance);
            zzZY4.set(null, zzwPValue);

            Class<?> zzXuRClass = Class.forName("com.aspose.words.zzXuR");
            Field zzWE8 = zzXuRClass.getDeclaredField("zzWE8");
            zzWE8.setAccessible(true);
            zzWE8.set(null, 128);
            Field zzZKj = zzXuRClass.getDeclaredField("zzZKj");
            zzZKj.setAccessible(true);
            zzZKj.set(null, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
