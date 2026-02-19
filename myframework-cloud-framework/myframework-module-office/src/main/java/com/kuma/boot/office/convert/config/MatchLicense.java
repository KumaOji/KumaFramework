/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.io.FileUtil
 *  com.aspose.words.License
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.core.io.ClassPathResource
 */
package com.kuma.boot.office.convert.config;

import cn.hutool.core.io.FileUtil;
import com.aspose.words.License;
import com.kuma.boot.common.utils.log.LogUtils;

import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;

public class MatchLicense {
    public static void init() {
        try {
            LogUtils.info((String)"\u5b9e\u73b0`aspose-words`\u6388\u6743 -> \u53bb\u6389\u5934\u90e8\u6c34\u5370", (Object[])new Object[0]);
            InputStream is = new ClassPathResource("license.xml").getInputStream();
            License license = new License();
            license.setLicense(is);
            FileUtil.mkdir((String)Constants.DEFAULT_FOLDER_TMP_GENERATE);
        }
        catch (Exception e) {
            LogUtils.error((String)"\u300a`aspose-words`\u6388\u6743\u300b \u5931\u8d25\uff1a {}", (Object[])new Object[]{e.getMessage()});
        }
    }
}

