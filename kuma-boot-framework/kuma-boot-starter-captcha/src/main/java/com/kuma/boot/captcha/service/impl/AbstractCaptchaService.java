//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;

public abstract class AbstractCaptchaService implements CaptchaService {
    protected static final String IMAGE_TYPE_PNG = "png";
    protected static int HAN_ZI_SIZE = 25;
    protected static int HAN_ZI_SIZE_HALF;
    protected static String REDIS_CAPTCHA_KEY;
    protected static String REDIS_SECOND_CAPTCHA_KEY;
    protected static Long EXPIRESIN_SECONDS;
    protected static Long EXPIRESIN_THREE;
    protected static String waterMark;
    protected static String waterMarkFontStr;
    protected Font waterMarkFont;
    protected static String slipOffset;
    protected static Boolean captchaAesStatus;
    protected static String clickWordFontStr;
    protected Font clickWordFont;
    protected static String cacheType;
    protected static int captchaInterferenceOptions;
    private static FrequencyLimitHandler limitHandler;

    public void init(final Properties config) {
        boolean aBoolean = Boolean.parseBoolean(config.getProperty("captcha.init.original"));
        if (!aBoolean) {
            ImageUtils.cacheImage(config.getProperty("captcha.captchaOriginalPath.jigsaw"), config.getProperty("captcha.captchaOriginalPath.pic-click"));
        }

        LogUtils.info("--->>>初始化验证码底图<<<---" + this.captchaType(), new Object[0]);
        waterMark = config.getProperty("captcha.water.mark", "我的水印");
        slipOffset = config.getProperty("captcha.slip.offset", "5");
        waterMarkFontStr = config.getProperty("captcha.water.font", "WenQuanZhengHei.ttf");
        captchaAesStatus = Boolean.parseBoolean(config.getProperty("captcha.aes.status", "true"));
        clickWordFontStr = config.getProperty("captcha.font.type", "WenQuanZhengHei.ttf");
        cacheType = config.getProperty("captcha.cacheType", "local");
        captchaInterferenceOptions = Integer.parseInt(config.getProperty("captcha.interference.options", "0"));
        this.loadWaterMarkFont();
        if ("local".equals(cacheType)) {
            LogUtils.info("初始化local缓存...", new Object[0]);
            CacheUtil.init(Integer.parseInt(config.getProperty("captcha.cache.number", "1000")), Long.parseLong(config.getProperty("captcha.timing.clear", "180")));
        }

        if ("1".equals(config.getProperty("captcha.history.data.clear.enable", "0"))) {
            LogUtils.info("历史资源清除开关...开启..." + this.captchaType(), new Object[0]);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> this.destroy(config)));
        }

        if ("1".equals(config.getProperty("captcha.req.frequency.limit.enable", "0")) && limitHandler == null) {
            LogUtils.info("接口分钟内限流开关...开启...", new Object[0]);
            limitHandler = new FrequencyLimitHandler.DefaultLimitHandler(config, this.getCacheService(cacheType));
        }

    }

    protected CaptchaCacheService getCacheService(String cacheType) {
        return CaptchaServiceFactory.getCache(cacheType);
    }

    public void destroy(Properties config) {
    }

    public Captcha get(Captcha captcha) {
        if (limitHandler != null) {
            captcha.setClientUid(this.getValidateClientId(captcha));
            limitHandler.validateGet(captcha);
        }

        return captcha;
    }

    public Captcha check(Captcha captcha) {
        if (limitHandler != null) {
            captcha.setClientUid(this.getValidateClientId(captcha));
            limitHandler.validateCheck(captcha);
        }

        return captcha;
    }

    public Captcha verification(Captcha captcha) {
        if (captcha == null) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError(new Object[]{"captcha"}));
        } else if (StrUtil.isEmpty(captcha.getCaptchaVerification())) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError(new Object[]{"captchaVerification"}));
        } else {
            if (limitHandler != null) {
                limitHandler.validateVerify(captcha);
            }

            return captcha;
        }
    }

    protected String getValidateClientId(Captcha req) {
        if (StrUtil.isNotEmpty(req.getBrowserInfo())) {
            return MD5Utils.encrypt(req.getBrowserInfo());
        } else {
            return StrUtil.isNotEmpty(req.getClientUid()) ? req.getClientUid() : null;
        }
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
            if (!waterMarkFontStr.toLowerCase().endsWith(".ttf") && !waterMarkFontStr.toLowerCase().endsWith(".kmc") && !waterMarkFontStr.toLowerCase().endsWith(".otf")) {
                this.waterMarkFont = new Font(waterMarkFontStr, 1, HAN_ZI_SIZE / 2);
            } else {
                this.waterMarkFont = Font.createFont(0, (InputStream)Objects.requireNonNull(this.getClass().getResourceAsStream("/fonts/" + waterMarkFontStr))).deriveFont(1, (float)(HAN_ZI_SIZE / 2));
            }
        } catch (Exception e) {
            LogUtils.error(e, "load font error:{}", new Object[]{e.getMessage()});
        }

    }

    public static boolean base64StrToImage(String imgStr, String path) {
        if (imgStr == null) {
            return false;
        } else {
            Base64.Decoder decoder = Base64.getDecoder();

            try {
                byte[] b = decoder.decode(imgStr);

                for(int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {
                        b[i] = (byte)(b[i] + 256);
                    }
                }

                File tempFile = new File(path);
                if (!tempFile.getParentFile().exists()) {
                    tempFile.getParentFile().mkdirs();
                }

                OutputStream out = new FileOutputStream(tempFile);
                out.write(b);
                out.flush();
                out.close();
                return true;
            } catch (Exception var6) {
                return false;
            }
        }
    }

    public static String decrypt(String point, String key) throws Exception {
        return AESUtils.decrypt(point, key);
    }

    protected static int getEnOrChLength(String s) {
        int enCount = 0;
        int chCount = 0;

        for(int i = 0; i < s.length(); ++i) {
            int length = String.valueOf(s.charAt(i)).getBytes(StandardCharsets.UTF_8).length;
            if (length > 1) {
                ++chCount;
            } else {
                ++enCount;
            }
        }

        int chOffset = HAN_ZI_SIZE / 2 * chCount + 5;
        int enOffset = enCount * 8;
        return chOffset + enOffset;
    }

    static {
        HAN_ZI_SIZE_HALF = HAN_ZI_SIZE / 2;
        REDIS_CAPTCHA_KEY = "RUNNING:CAPTCHA:%s";
        REDIS_SECOND_CAPTCHA_KEY = "RUNNING:CAPTCHA:second-%s";
        EXPIRESIN_SECONDS = 120L;
        EXPIRESIN_THREE = 180L;
        waterMark = "我的水印";
        waterMarkFontStr = "WenQuanZhengHei.ttf";
        slipOffset = "5";
        captchaAesStatus = true;
        clickWordFontStr = "WenQuanZhengHei.ttf";
        cacheType = "local";
        captchaInterferenceOptions = 0;
    }
}
