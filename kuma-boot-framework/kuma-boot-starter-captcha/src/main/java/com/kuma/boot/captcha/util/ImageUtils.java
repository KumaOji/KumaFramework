//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.util;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.captcha.model.CaptchaBaseEnum;
import com.kuma.boot.common.utils.common.RandomUtils;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.awt.image.BufferedImage;
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
    private static Map<String, String> originalCacheMap = new ConcurrentHashMap();
    private static Map<String, String> slidingBlockCacheMap = new ConcurrentHashMap();
    private static Map<String, String> picClickCacheMap = new ConcurrentHashMap();
    private static Map<String, String[]> fileNameMap = new ConcurrentHashMap();

    public static void cacheImage(String captchaOriginalPathJigsaw, String captchaOriginalPathClick) {
        if (StrUtil.isBlank(captchaOriginalPathJigsaw)) {
            originalCacheMap.putAll(getResourcesImagesFile("defaultImages/jigsaw/original"));
            slidingBlockCacheMap.putAll(getResourcesImagesFile("defaultImages/jigsaw/slidingBlock"));
        } else {
            originalCacheMap.putAll(getImagesFile(captchaOriginalPathJigsaw + File.separator + "original"));
            slidingBlockCacheMap.putAll(getImagesFile(captchaOriginalPathJigsaw + File.separator + "slidingBlock"));
        }

        if (StrUtil.isBlank(captchaOriginalPathClick)) {
            picClickCacheMap.putAll(getResourcesImagesFile("defaultImages/pic-click"));
        } else {
            picClickCacheMap.putAll(getImagesFile(captchaOriginalPathClick));
        }

        fileNameMap.put(CaptchaBaseEnum.ORIGINAL.getCodeValue(), (String[])originalCacheMap.keySet().toArray(new String[0]));
        fileNameMap.put(CaptchaBaseEnum.SLIDING_BLOCK.getCodeValue(), (String[])slidingBlockCacheMap.keySet().toArray(new String[0]));
        fileNameMap.put(CaptchaBaseEnum.PIC_CLICK.getCodeValue(), (String[])picClickCacheMap.keySet().toArray(new String[0]));
        LogUtils.info("初始化底图:{}", new Object[]{JacksonUtils.toJSONString(fileNameMap)});
    }

    public static void cacheBootImage(Map<String, String> originalMap, Map<String, String> slidingBlockMap, Map<String, String> picClickMap) {
        originalCacheMap.putAll(originalMap);
        slidingBlockCacheMap.putAll(slidingBlockMap);
        picClickCacheMap.putAll(picClickMap);
        fileNameMap.put(CaptchaBaseEnum.ORIGINAL.getCodeValue(), (String[])originalCacheMap.keySet().toArray(new String[0]));
        fileNameMap.put(CaptchaBaseEnum.SLIDING_BLOCK.getCodeValue(), (String[])slidingBlockCacheMap.keySet().toArray(new String[0]));
        fileNameMap.put(CaptchaBaseEnum.PIC_CLICK.getCodeValue(), (String[])picClickCacheMap.keySet().toArray(new String[0]));
        LogUtils.info("自定义resource底图:{}", new Object[]{JacksonUtils.toJSONString(fileNameMap)});
    }

    public static BufferedImage getOriginal() {
        String[] strings = (String[])fileNameMap.get(CaptchaBaseEnum.ORIGINAL.getCodeValue());
        if (null != strings && strings.length != 0) {
            int randomInt = RandomUtils.randomInt(0, strings.length);
            String s = (String)originalCacheMap.get(strings[randomInt]);
            return getBase64StrToImage(s);
        } else {
            return null;
        }
    }

    public static String getslidingBlock() {
        String[] strings = (String[])fileNameMap.get(CaptchaBaseEnum.SLIDING_BLOCK.getCodeValue());
        if (null != strings && strings.length != 0) {
            int randomInt = RandomUtils.randomInt(0, strings.length);
            return (String)slidingBlockCacheMap.get(strings[randomInt]);
        } else {
            return null;
        }
    }

    public static BufferedImage getPicClick() {
        String[] strings = (String[])fileNameMap.get(CaptchaBaseEnum.PIC_CLICK.getCodeValue());
        if (null != strings && strings.length != 0) {
            int randomInt = RandomUtils.randomInt(0, strings.length);
            String s = (String)picClickCacheMap.get(strings[randomInt]);
            return getBase64StrToImage(s);
        } else {
            return null;
        }
    }

    public static String getImageToBase64Str(BufferedImage templateImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(templateImage, "png", baos);
        } catch (IOException e) {
            LogUtils.error(e);
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
        } catch (IOException e) {
            LogUtils.error(e);
            return null;
        }
    }

    private static Map<String, String> getResourcesImagesFile(String path) {
        Map<String, String> imgMap = new HashMap();
        ClassLoader classLoader = ImageUtils.class.getClassLoader();

        for(int i = 1; i <= 6; ++i) {
            InputStream resourceAsStream = classLoader.getResourceAsStream(path.concat("/").concat(String.valueOf(i).concat(".png")));
            byte[] bytes = new byte[0];

            try {
                bytes = FileCopyUtils.copyToByteArray(resourceAsStream);
            } catch (IOException e) {
                LogUtils.error(e);
            }

            String string = Base64.getEncoder().encodeToString(bytes);
            String filename = String.valueOf(i).concat(".png");
            imgMap.put(filename, string);
        }

        return imgMap;
    }

    private static Map<String, String> getImagesFile(String path) {
        Map<String, String> imgMap = new HashMap();
        File file = new File(path);
        if (!file.exists()) {
            return new HashMap();
        } else {
            File[] files = file.listFiles();

            assert files != null;

            Arrays.stream(files).forEach((item) -> {
                try {
                    FileInputStream fileInputStream = new FileInputStream(item);
                    byte[] bytes = FileCopyUtils.copyToByteArray(fileInputStream);
                    String string = Base64.getEncoder().encodeToString(bytes);
                    imgMap.put(item.getName(), string);
                } catch (IOException e) {
                    LogUtils.error(e);
                }

            });
            return imgMap;
        }
    }
}
