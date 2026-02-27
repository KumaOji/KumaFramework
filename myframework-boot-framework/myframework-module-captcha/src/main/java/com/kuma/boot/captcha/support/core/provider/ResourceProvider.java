/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.codec.Base64
 *  cn.hutool.core.img.FontUtil
 *  cn.hutool.core.img.ImgUtil
 *  cn.hutool.core.io.FileUtil
 *  cn.hutool.core.io.IORuntimeException
 *  cn.hutool.core.util.IdUtil
 *  cn.hutool.system.SystemUtil
 *  com.kuma.boot.common.utils.io.ResourceUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.commons.collections4.MapUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.io.Resource
 *  org.springframework.stereotype.Component
 *  org.springframework.util.FileCopyUtils
 */
package com.kuma.boot.captcha.support.core.provider;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.FontUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.IdUtil;
import cn.hutool.system.SystemUtil;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaResource;
import com.kuma.boot.captcha.support.core.definition.enums.FontStyle;
import com.kuma.boot.captcha.support.core.properties.CaptchaProperties;
import com.kuma.boot.common.utils.io.ResourceUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

@Component
public class ResourceProvider
implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(ResourceProvider.class);
    private static final String FONT_RESOURCE = "classpath*:/fonts/*.ttf";
    private static final String FONT_FOLDER = "/usr/share/fonts/herodotus/";
    private final Map<String, String[]> imageIndexes = new ConcurrentHashMap<String, String[]>();
    private final Map<String, String> jigsawOriginalImages = new ConcurrentHashMap<String, String>();
    private final Map<String, String> jigsawTemplateImages = new ConcurrentHashMap<String, String>();
    private final Map<String, String> wordClickImages = new ConcurrentHashMap<String, String>();
    private Map<String, Font> fonts = new ConcurrentHashMap<String, Font>();
    private final CaptchaProperties captchaProperties;

    public ResourceProvider(CaptchaProperties captchaProperties) {
        this.captchaProperties = captchaProperties;
    }

    public CaptchaProperties getCaptchaProperties() {
        return this.captchaProperties;
    }

    public void afterPropertiesSet() throws Exception {
        String systemName = SystemUtil.getOsInfo().getName();
        log.debug("[ttc] |- Before captcha resource loading, check system. Current system is [{}]", (Object)systemName);
        log.debug("[ttc] |- Captcha resource loading is BEGIN\uff01");
        this.loadImages(this.jigsawOriginalImages, this.getCaptchaProperties().getJigsaw().getOriginalResource(), CaptchaResource.JIGSAW_ORIGINAL);
        this.loadImages(this.jigsawTemplateImages, this.getCaptchaProperties().getJigsaw().getTemplateResource(), CaptchaResource.JIGSAW_TEMPLATE);
        this.loadImages(this.wordClickImages, this.getCaptchaProperties().getWordClick().getImageResource(), CaptchaResource.WORD_CLICK);
        this.loadFonts();
        log.debug("[ttc] |- Jigsaw captcha resource loading is END\uff01");
    }

    private static String getBase64Image(Resource resource) {
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] bytes = FileCopyUtils.copyToByteArray((InputStream)inputStream);
            return Base64.encode((byte[])bytes);
        }
        catch (IOException e) {
            log.error("[ttc] |- Captcha get image catch io error!", (Throwable)e);
            return null;
        }
    }

    private static Map<String, String> getImages(String location) {
        if (ResourceUtils.isClasspathAllUrl((String)location)) {
            try {
                Object[] resources = ResourceUtils.getResources((String)location);
                ConcurrentHashMap<String, String> images = new ConcurrentHashMap<String, String>();
                if (ArrayUtils.isNotEmpty((Object[])resources)) {
                    Arrays.stream(resources).forEach(resource -> {
                        String data = ResourceProvider.getBase64Image(resource);
                        if (StringUtils.isNotBlank((String)data)) {
                            images.put(IdUtil.fastSimpleUUID(), data);
                        }
                    });
                }
                return images;
            }
            catch (IOException e) {
                log.error("[ttc] |- Analysis the  location [{}] catch io error!", (Object)location, (Object)e);
            }
        }
        return new ConcurrentHashMap<String, String>(8);
    }

    private void loadImages(Map<String, String> container, String location, CaptchaResource captchaResource) {
        Map<String, String> resource = ResourceProvider.getImages(location);
        if (MapUtils.isNotEmpty(resource)) {
            container.putAll(resource);
            log.debug("[ttc] |- {} load complete, total number is [{}]", (Object)captchaResource.getContent(), (Object)resource.size());
            this.imageIndexes.put(captchaResource.name(), resource.keySet().toArray(new String[0]));
        }
    }

    private static Font getFont(Resource resource) {
        try {
            return FontUtil.createFont((InputStream)resource.getInputStream());
        }
        catch (IORuntimeException e) {
            log.warn("[ttc] |- Can not read font in the resources folder, maybe in docker.");
            Font fontInfileSystem = ResourceProvider.getFontUnderDocker(resource.getFilename());
            if (ObjectUtils.isNotEmpty((Object)fontInfileSystem)) {
                return fontInfileSystem;
            }
        }
        catch (IOException e) {
            log.error("[ttc] |- Resource object in resources folder catch io error!", (Throwable)e);
        }
        return null;
    }

    private static Font getFontUnderDocker(String filename) {
        String path;
        File file;
        if (SystemUtil.getOsInfo().isLinux() && ObjectUtils.isNotEmpty((Object)(file = new File(path = FONT_FOLDER + filename))) && FileUtil.exist((File)file)) {
            LogUtils.info((String)file.getAbsolutePath(), (Object[])new Object[0]);
            try {
                Font font = FontUtil.createFont((File)file);
                log.debug("[ttc] |- Read font [{}] under the DOCKER.", (Object)font.getFontName());
                return font;
            }
            catch (IORuntimeException e) {
                log.error("[ttc] |- Read font under the DOCKER catch error.");
            }
            catch (NullPointerException e) {
                log.error("[ttc] |- Read font under the DOCKER catch null error.");
            }
        }
        return null;
    }

    private static Map<String, Font> getFonts(String location) {
        if (ResourceUtils.isClasspathAllUrl((String)location)) {
            try {
                Object[] resources = ResourceUtils.getResources((String)location);
                ConcurrentHashMap<String, Font> fonts = new ConcurrentHashMap<String, Font>();
                if (ArrayUtils.isNotEmpty((Object[])resources)) {
                    Arrays.stream(resources).forEach(resource -> {
                        Font font = ResourceProvider.getFont(resource);
                        if (ObjectUtils.isNotEmpty((Object)font)) {
                            fonts.put(resource.getFilename(), font);
                        }
                    });
                }
                return fonts;
            }
            catch (IOException e) {
                log.error("[ttc] |- Analysis the  location [{}] catch io error!", (Object)location, (Object)e);
            }
        }
        return new ConcurrentHashMap<String, Font>(8);
    }

    private void loadFonts() {
        if (MapUtils.isEmpty(this.fonts)) {
            this.fonts = ResourceProvider.getFonts(FONT_RESOURCE);
            log.debug("[ttc] |- Font load complete, total number is [{}]", (Object)this.fonts.size());
        }
    }

    private Font getDefaultFont(String fontName, int fontSize, FontStyle fontStyle) {
        if (StringUtils.isNotBlank((String)fontName)) {
            return new Font(fontName, fontStyle.getMapping(), fontSize);
        }
        return new Font("WenQuanYi Zen Hei", fontStyle.getMapping(), fontSize);
    }

    public Font getFont(String fontName, int fontSize, FontStyle fontStyle) {
        if (MapUtils.isEmpty(this.fonts) || ObjectUtils.isEmpty((Object)this.fonts.get(fontName))) {
            return this.getDefaultFont(fontName, fontSize, fontStyle);
        }
        return this.fonts.get(fontName).deriveFont(fontStyle.getMapping(), Integer.valueOf(fontSize).floatValue());
    }

    public Font getFont(String fontName) {
        return this.getFont(fontName, 32, FontStyle.BOLD);
    }

    public Font getGraphicFont() {
        String fontName = this.getCaptchaProperties().getGraphics().getFont().getFontName();
        return this.getFont(fontName);
    }

    public Font getWaterMarkFont(int fontSize) {
        String fontName = this.getCaptchaProperties().getWatermark().getFontName();
        FontStyle fontStyle = this.getCaptchaProperties().getWatermark().getFontStyle();
        return this.getFont(fontName, fontSize, fontStyle);
    }

    public Font getChineseFont() {
        return this.getFont("WenQuanYi Zen Hei", 25, FontStyle.PLAIN);
    }

    private String getRandomBase64Image(Map<String, String> container, CaptchaResource captchaResource) {
        Object[] data = this.imageIndexes.get(captchaResource.name());
        if (ArrayUtils.isNotEmpty((Object[])data)) {
            int randomInt = RandomProvider.randomInt(0, data.length);
            return container.get(data[randomInt]);
        }
        return null;
    }

    protected BufferedImage getRandomImage(Map<String, String> container, CaptchaResource captchaResource) {
        String data = this.getRandomBase64Image(container, captchaResource);
        if (StringUtils.isNotBlank((String)data)) {
            return ImgUtil.toImage((String)data);
        }
        return null;
    }

    public String getRandomBase64OriginalImage() {
        return this.getRandomBase64Image(this.jigsawOriginalImages, CaptchaResource.JIGSAW_ORIGINAL);
    }

    public String getRandomBase64TemplateImage() {
        return this.getRandomBase64Image(this.jigsawTemplateImages, CaptchaResource.JIGSAW_TEMPLATE);
    }

    public BufferedImage getRandomOriginalImage() {
        return this.getRandomImage(this.jigsawOriginalImages, CaptchaResource.JIGSAW_ORIGINAL);
    }

    public BufferedImage getRandomTemplateImage() {
        return this.getRandomImage(this.jigsawOriginalImages, CaptchaResource.JIGSAW_ORIGINAL);
    }

    public BufferedImage getRandomWordClickImage() {
        return this.getRandomImage(this.wordClickImages, CaptchaResource.WORD_CLICK);
    }
}

