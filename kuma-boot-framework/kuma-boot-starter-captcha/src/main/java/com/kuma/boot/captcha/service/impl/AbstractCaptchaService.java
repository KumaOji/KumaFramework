/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.secure.AESUtils
 *  com.kuma.boot.common.utils.secure.MD5Utils
 */
package com.kuma.boot.captcha.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.captcha.model.Captcha;
import com.kuma.boot.captcha.model.CaptchaCodeEnum;
import com.kuma.boot.captcha.model.CaptchaException;
import com.kuma.boot.captcha.service.CaptchaCacheService;
import com.kuma.boot.captcha.service.CaptchaService;
import com.kuma.boot.captcha.util.CacheUtil;
import com.kuma.boot.captcha.util.ImageUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.secure.AESUtils;
import com.kuma.boot.common.utils.secure.MD5Utils;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;

public abstract class AbstractCaptchaService
implements CaptchaService {
    protected static final String IMAGE_TYPE_PNG = "png";
    protected static int HAN_ZI_SIZE = 25;
    protected static int HAN_ZI_SIZE_HALF = HAN_ZI_SIZE / 2;
    protected static String REDIS_CAPTCHA_KEY = "RUNNING:CAPTCHA:%s";
    protected static String REDIS_SECOND_CAPTCHA_KEY = "RUNNING:CAPTCHA:second-%s";
    protected static Long EXPIRESIN_SECONDS = 120L;
    protected static Long EXPIRESIN_THREE = 180L;
    protected static String waterMark = "\u6211\u7684\u6c34\u5370";
    protected static String waterMarkFontStr = "WenQuanZhengHei.ttf";
    protected Font waterMarkFont;
    protected static String slipOffset = "5";
    protected static Boolean captchaAesStatus = true;
    protected static String clickWordFontStr = "WenQuanZhengHei.ttf";
    protected Font clickWordFont;
    protected static String cacheType = "local";
    protected static int captchaInterferenceOptions = 0;
    private static FrequencyLimitHandler limitHandler;

    @Override
    public void init(Properties config) {
        boolean aBoolean = Boolean.parseBoolean(config.getProperty("captcha.init.original"));
        if (!aBoolean) {
            ImageUtils.cacheImage(config.getProperty("captcha.captchaOriginalPath.jigsaw"), config.getProperty("captcha.captchaOriginalPath.pic-click"));
        }
        LogUtils.info((String)("--->>>\u521d\u59cb\u5316\u9a8c\u8bc1\u7801\u5e95\u56fe<<<---" + this.captchaType()), (Object[])new Object[0]);
        waterMark = config.getProperty("captcha.water.mark", "\u6211\u7684\u6c34\u5370");
        slipOffset = config.getProperty("captcha.slip.offset", "5");
        waterMarkFontStr = config.getProperty("captcha.water.font", "WenQuanZhengHei.ttf");
        captchaAesStatus = Boolean.parseBoolean(config.getProperty("captcha.aes.status", "true"));
        clickWordFontStr = config.getProperty("captcha.font.type", "WenQuanZhengHei.ttf");
        cacheType = config.getProperty("captcha.cacheType", "local");
        captchaInterferenceOptions = Integer.parseInt(config.getProperty("captcha.interference.options", "0"));
        this.loadWaterMarkFont();
        if ("local".equals(cacheType)) {
            LogUtils.info((String)"\u521d\u59cb\u5316local\u7f13\u5b58...", (Object[])new Object[0]);
            CacheUtil.init(Integer.parseInt(config.getProperty("captcha.cache.number", "1000")), Long.parseLong(config.getProperty("captcha.timing.clear", "180")));
        }
        if ("1".equals(config.getProperty("captcha.history.data.clear.enable", "0"))) {
            LogUtils.info((String)("\u5386\u53f2\u8d44\u6e90\u6e05\u9664\u5f00\u5173...\u5f00\u542f..." + this.captchaType()), (Object[])new Object[0]);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> this.destroy(config)));
        }
        if ("1".equals(config.getProperty("captcha.req.frequency.limit.enable", "0")) && limitHandler == null) {
            LogUtils.info((String)"\u63a5\u53e3\u5206\u949f\u5185\u9650\u6d41\u5f00\u5173...\u5f00\u542f...", (Object[])new Object[0]);
            limitHandler = new FrequencyLimitHandler.DefaultLimitHandler(config, this.getCacheService(cacheType));
        }
    }

    protected CaptchaCacheService getCacheService(String cacheType) {
        return CaptchaServiceFactory.getCache(cacheType);
    }

    @Override
    public void destroy(Properties config) {
    }

    @Override
    public Captcha get(Captcha captcha) {
        if (limitHandler != null) {
            captcha.setClientUid(this.getValidateClientId(captcha));
            limitHandler.validateGet(captcha);
        }
        return captcha;
    }

    @Override
    public Captcha check(Captcha captcha) {
        if (limitHandler != null) {
            captcha.setClientUid(this.getValidateClientId(captcha));
            limitHandler.validateCheck(captcha);
        }
        return captcha;
    }

    @Override
    public Captcha verification(Captcha captcha) {
        if (captcha == null) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError("captcha"));
        }
        if (StrUtil.isEmpty((CharSequence)captcha.getCaptchaVerification())) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError("captchaVerification"));
        }
        if (limitHandler != null) {
            limitHandler.validateVerify(captcha);
        }
        return captcha;
    }

    protected String getValidateClientId(Captcha req) {
        if (StrUtil.isNotEmpty((CharSequence)req.getBrowserInfo())) {
            return MD5Utils.encrypt((String)req.getBrowserInfo());
        }
        if (StrUtil.isNotEmpty((CharSequence)req.getClientUid())) {
            return req.getClientUid();
        }
        return null;
    }

    protected void afterValidateFail(Captcha data) {
        if (limitHandler != null) {
            String fails = String.format("AJ.CAPTCHA.REQ.LIMIT-%s-%s", "FAIL", data.getClientUid());
            CaptchaCacheService cs = this.getCacheService(cacheType);
            if (!cs.exists(fails)) {
                cs.set(fails, "1", 60L);
            }
            cs.increment(fails, 1L);
        }
    }

    private void loadWaterMarkFont() {
        try {
            this.waterMarkFont = waterMarkFontStr.toLowerCase().endsWith(".ttf") || waterMarkFontStr.toLowerCase().endsWith(".kmc") || waterMarkFontStr.toLowerCase().endsWith(".otf") ? Font.createFont(0, Objects.requireNonNull(this.getClass().getResourceAsStream("/fonts/" + waterMarkFontStr))).deriveFont(1, HAN_ZI_SIZE / 2) : new Font(waterMarkFontStr, 1, HAN_ZI_SIZE / 2);
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e, (String)"load font error:{}", (Object[])new Object[]{e.getMessage()});
        }
    }

    public static boolean base64StrToImage(String imgStr, String path) {
        if (imgStr == null) {
            return false;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            byte[] b = decoder.decode(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] >= 0) continue;
                int n = i;
                b[n] = (byte)(b[n] + 256);
            }
            File tempFile = new File(path);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(tempFile);
            ((OutputStream)out).write(b);
            out.flush();
            ((OutputStream)out).close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static String decrypt(String point, String key) throws Exception {
        return AESUtils.decrypt((String)point, (String)key);
    }

    protected static int getEnOrChLength(String s) {
        int enCount = 0;
        int chCount = 0;
        for (int i = 0; i < s.length(); ++i) {
            int length = String.valueOf(s.charAt(i)).getBytes(StandardCharsets.UTF_8).length;
            if (length > 1) {
                ++chCount;
                continue;
            }
            ++enCount;
        }
        int chOffset = HAN_ZI_SIZE / 2 * chCount + 5;
        int enOffset = enCount * 8;
        return chOffset + enOffset;
    }
}

