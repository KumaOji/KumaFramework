//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.autoconfigure;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.captcha.autoconfigure.properties.CaptchaProperties;
import com.kuma.boot.captcha.service.CaptchaCacheService;
import com.kuma.boot.captcha.service.CaptchaService;
import com.kuma.boot.captcha.service.impl.CaptchaServiceFactory;
import com.kuma.boot.captcha.util.ImageUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

@AutoConfiguration
@EnableConfigurationProperties({CaptchaProperties.class})
@ConditionalOnProperty(
        prefix = "kuma.boot.captcha",
        name = {"enabled"},
        havingValue = "true"
)
public class CaptchaAutoConfiguration implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(CaptchaAutoConfiguration.class, "kuma-boot-starter-captcha", new String[0]);
    }

    @Bean
    @ConditionalOnMissingBean
    public CaptchaService captchaService(CaptchaProperties prop) {
        Properties config = new Properties();
        config.put("captcha.cacheType", prop.getCacheType().name());
        config.put("captcha.water.mark", prop.getWaterMark());
        config.put("captcha.font.type", prop.getFontType());
        config.put("captcha.type", prop.getType().getCodeValue());
        config.put("captcha.interference.options", prop.getInterferenceOptions());
        config.put("captcha.captchaOriginalPath.jigsaw", prop.getJigsaw());
        config.put("captcha.captchaOriginalPath.pic-click", prop.getPicClick());
        config.put("captcha.slip.offset", prop.getSlipOffset());
        config.put("captcha.aes.status", String.valueOf(prop.getAesStatus()));
        config.put("captcha.water.font", prop.getWaterFont());
        config.put("captcha.cache.number", prop.getCacheNumber());
        config.put("captcha.timing.clear", prop.getTimingClear());
        config.put("captcha.history.data.clear.enable", prop.getHistoryDataClearEnable() ? "1" : "0");
        config.put("captcha.req.frequency.limit.enable", prop.getReqFrequencyLimitEnable() ? "1" : "0");
        config.put("captcha.req.get.lock.limit", "" + prop.getReqGetLockLimit());
        config.put("captcha.req.get.lock.seconds", "" + prop.getReqGetLockSeconds());
        config.put("captcha.req.get.minute.limit", "" + prop.getReqGetMinuteLimit());
        config.put("captcha.req.check.minute.limit", "" + prop.getReqCheckMinuteLimit());
        config.put("captcha.req.verify.minute.limit", "" + prop.getReqVerifyMinuteLimit());
        if (StrUtil.isNotBlank(prop.getJigsaw()) && prop.getJigsaw().startsWith("classpath:") || StrUtil.isNotBlank(prop.getPicClick()) && prop.getPicClick().startsWith("classpath:")) {
            config.put("captcha.init.original", "true");
            initializeBaseMap(prop.getJigsaw(), prop.getPicClick());
        }

        return CaptchaServiceFactory.getInstance(config);
    }

    @Bean
    public CaptchaCacheService captchaCacheService(CaptchaProperties captchaProperties) {
        return CaptchaServiceFactory.getCache(captchaProperties.getCacheType().name());
    }

    public static void initializeBaseMap(String jigsaw, String picClick) {
        ImageUtils.cacheBootImage(getResourcesImagesFile(jigsaw + "/original/*.png"), getResourcesImagesFile(jigsaw + "/slidingBlock/*.png"), getResourcesImagesFile(picClick + "/*.png"));
    }

    public static Map<String, String> getResourcesImagesFile(String path) {
        Map<String, String> imgMap = new HashMap<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources(path);

            for(Resource resource : resources) {
                byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
                String string = Base64.getEncoder().encodeToString(bytes);
                String filename = resource.getFilename();
                imgMap.put(filename, string);
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }

        return imgMap;
    }
}
