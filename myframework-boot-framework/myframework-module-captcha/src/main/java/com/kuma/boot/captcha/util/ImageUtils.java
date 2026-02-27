/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.utils.common.RandomUtils
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.captcha.util;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.captcha.model.CaptchaBaseEnum;
import com.kuma.boot.common.utils.common.RandomUtils;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

public class ImageUtils {
    private static Map<String, String> originalCacheMap = new ConcurrentHashMap<String, String>();
    private static Map<String, String> slidingBlockCacheMap = new ConcurrentHashMap<String, String>();
    private static Map<String, String> picClickCacheMap = new ConcurrentHashMap<String, String>();
    private static Map<String, String[]> fileNameMap = new ConcurrentHashMap<String, String[]>();

    public static void cacheImage(String captchaOriginalPathJigsaw, String captchaOriginalPathClick) {
        if (StrUtil.isBlank((CharSequence)captchaOriginalPathJigsaw)) {
            originalCacheMap.putAll(ImageUtils.getResourcesImagesFile("defaultImages/jigsaw/original"));
            slidingBlockCacheMap.putAll(ImageUtils.getResourcesImagesFile("defaultImages/jigsaw/slidingBlock"));
        } else {
            originalCacheMap.putAll(ImageUtils.getImagesFile(captchaOriginalPathJigsaw + File.separator + "original"));
            slidingBlockCacheMap.putAll(ImageUtils.getImagesFile(captchaOriginalPathJigsaw + File.separator + "slidingBlock"));
        }
        if (StrUtil.isBlank((CharSequence)captchaOriginalPathClick)) {
            picClickCacheMap.putAll(ImageUtils.getResourcesImagesFile("defaultImages/pic-click"));
        } else {
            picClickCacheMap.putAll(ImageUtils.getImagesFile(captchaOriginalPathClick));
        }
        fileNameMap.put(CaptchaBaseEnum.ORIGINAL.getCodeValue(), originalCacheMap.keySet().toArray(new String[0]));
        fileNameMap.put(CaptchaBaseEnum.SLIDING_BLOCK.getCodeValue(), slidingBlockCacheMap.keySet().toArray(new String[0]));
        fileNameMap.put(CaptchaBaseEnum.PIC_CLICK.getCodeValue(), picClickCacheMap.keySet().toArray(new String[0]));
        LogUtils.info((String)"\u521d\u59cb\u5316\u5e95\u56fe:{}", (Object[])new Object[]{JacksonUtils.toJSONString(fileNameMap)});
    }

    public static void cacheBootImage(Map<String, String> originalMap, Map<String, String> slidingBlockMap, Map<String, String> picClickMap) {
        originalCacheMap.putAll(originalMap);
        slidingBlockCacheMap.putAll(slidingBlockMap);
        picClickCacheMap.putAll(picClickMap);
        fileNameMap.put(CaptchaBaseEnum.ORIGINAL.getCodeValue(), originalCacheMap.keySet().toArray(new String[0]));
        fileNameMap.put(CaptchaBaseEnum.SLIDING_BLOCK.getCodeValue(), slidingBlockCacheMap.keySet().toArray(new String[0]));
        fileNameMap.put(CaptchaBaseEnum.PIC_CLICK.getCodeValue(), picClickCacheMap.keySet().toArray(new String[0]));
        LogUtils.info((String)"\u81ea\u5b9a\u4e49resource\u5e95\u56fe:{}", (Object[])new Object[]{JacksonUtils.toJSONString(fileNameMap)});
    }

    public static BufferedImage getOriginal() {
        String[] strings = fileNameMap.get(CaptchaBaseEnum.ORIGINAL.getCodeValue());
        if (null == strings || strings.length == 0) {
            return null;
        }
        int randomInt = RandomUtils.randomInt((int)0, (int)strings.length);
        String s = originalCacheMap.get(strings[randomInt]);
        return ImageUtils.getBase64StrToImage(s);
    }

    public static String getslidingBlock() {
        String[] strings = fileNameMap.get(CaptchaBaseEnum.SLIDING_BLOCK.getCodeValue());
        if (null == strings || strings.length == 0) {
            return null;
        }
        int randomInt = RandomUtils.randomInt((int)0, (int)strings.length);
        return slidingBlockCacheMap.get(strings[randomInt]);
    }

    public static BufferedImage getPicClick() {
        String[] strings = fileNameMap.get(CaptchaBaseEnum.PIC_CLICK.getCodeValue());
        if (null == strings || strings.length == 0) {
            return null;
        }
        int randomInt = RandomUtils.randomInt((int)0, (int)strings.length);
        String s = picClickCacheMap.get(strings[randomInt]);
        return ImageUtils.getBase64StrToImage(s);
    }

    public static String getImageToBase64Str(BufferedImage templateImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write((RenderedImage)templateImage, "png", baos);
        }
        catch (IOException e) {
            LogUtils.error((Throwable)e);
        }
        byte[] bytes = baos.toByteArray();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes).trim();
    }

    public static BufferedImage getBase64StrToImage(String base64String) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] bytes = decoder.decode(base64String);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            return ImageIO.read(inputStream);
        }
        catch (IOException e) {
            LogUtils.error((Throwable)e);
            return null;
        }
    }

    private static Map<String, String> getResourcesImagesFile(String path) {
        HashMap<String, String> imgMap = new HashMap<String, String>();
        ClassLoader classLoader = ImageUtils.class.getClassLoader();
        for (int i = 1; i <= 6; ++i) {
            InputStream resourceAsStream = classLoader.getResourceAsStream(path.concat("/").concat(String.valueOf(i).concat(".png")));
            byte[] bytes = new byte[]{};
            try {
                bytes = FileCopyUtils.copyToByteArray(resourceAsStream);
            }
            catch (IOException e) {
                LogUtils.error((Throwable)e);
            }
            String string = Base64.getEncoder().encodeToString(bytes);
            String filename = String.valueOf(i).concat(".png");
            imgMap.put(filename, string);
        }
        return imgMap;
    }

    private static Map<String, String> getImagesFile(String path) {
        HashMap<String, String> imgMap = new HashMap<String, String>();
        File file = new File(path);
        if (!file.exists()) {
            return new HashMap<String, String>();
        }
        File[] files = file.listFiles();
        assert (files != null);
        Arrays.stream(files).forEach(item -> {
            try {
                FileInputStream fileInputStream = new FileInputStream((File)item);
                byte[] bytes = FileCopyUtils.copyToByteArray(fileInputStream);
                String string = Base64.getEncoder().encodeToString(bytes);
                imgMap.put(item.getName(), string);
            }
            catch (IOException e) {
                LogUtils.error((Throwable)e);
            }
        });
        return imgMap;
    }
}

