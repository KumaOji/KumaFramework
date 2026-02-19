/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.aspose.words.License
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.CommandLineRunner
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.office.convert.config;

import com.aspose.words.License;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.InputStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class StartAppRunner
implements CommandLineRunner {
    public void run(String ... args) throws Exception {
        LogUtils.info((String)"\u300a\u670d\u52a1\u521d\u59cb\u5316\u6267\u884c\u5904\u7406\u300b start...", (Object[])new Object[0]);
        try {
            LogUtils.info((String)"\u5b9e\u73b0`aspose-words`\u6388\u6743 -> \u53bb\u6389\u5934\u90e8\u6c34\u5370", (Object[])new Object[0]);
            InputStream is = new ClassPathResource("license.xml").getInputStream();
            License license = new License();
            license.setLicense(is);
        }
        catch (Exception e) {
            LogUtils.error((String)"\u300a`aspose-words`\u6388\u6743\u300b \u5931\u8d25\uff1a {}", (Object[])new Object[]{e.getMessage()});
        }
        LogUtils.info((String)"\u300a\u670d\u52a1\u521d\u59cb\u5316\u6267\u884c\u5904\u7406\u300b end...", (Object[])new Object[0]);
    }
}

