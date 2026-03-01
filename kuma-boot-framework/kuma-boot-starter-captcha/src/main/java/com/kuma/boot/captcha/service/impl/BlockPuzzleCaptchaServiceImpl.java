/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.utils.common.RandomUtils
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.secure.AESUtils
 */
package com.kuma.boot.captcha.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.captcha.model.Captcha;
import com.kuma.boot.captcha.model.CaptchaCodeEnum;
import com.kuma.boot.captcha.model.CaptchaException;
import com.kuma.boot.captcha.model.CaptchaTypeEnum;
import com.kuma.boot.captcha.model.Point;
import com.kuma.boot.captcha.util.ImageUtils;
import com.kuma.boot.common.utils.common.RandomUtils;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.secure.AESUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;

public class BlockPuzzleCaptchaServiceImpl
extends AbstractCaptchaService {
    @Override
    public void init(Properties config) {
        super.init(config);
    }

    @Override
    public void destroy(Properties config) {
        LogUtils.info((String)"start-clear-history-data-}", (Object[])new Object[]{this.captchaType()});
    }

    @Override
    public String captchaType() {
        return CaptchaTypeEnum.BLOCKPUZZLE.getCodeValue();
    }

    @Override
    public Captcha get(Captcha captchaVO) {
        super.get(captchaVO);
        BufferedImage originalImage = ImageUtils.getOriginal();
        if (null == originalImage) {
            LogUtils.error((String)"\u6ed1\u52a8\u5e95\u56fe\u672a\u521d\u59cb\u5316\u6210\u529f\uff0c\u8bf7\u68c0\u67e5\u8def\u5f84", (Object[])new Object[0]);
            throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_BASEMAP_NULL);
        }
        Graphics backgroundGraphics = originalImage.getGraphics();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        backgroundGraphics.setFont(this.waterMarkFont);
        backgroundGraphics.setColor(Color.white);
        backgroundGraphics.drawString(waterMark, width - BlockPuzzleCaptchaServiceImpl.getEnOrChLength(waterMark), height - HAN_ZI_SIZE / 2 + 7);
        String jigsawImageBase64 = ImageUtils.getslidingBlock();
        BufferedImage jigsawImage = ImageUtils.getBase64StrToImage(jigsawImageBase64);
        if (null == jigsawImage) {
            LogUtils.error((String)"\u6ed1\u52a8\u5e95\u56fe\u672a\u521d\u59cb\u5316\u6210\u529f\uff0c\u8bf7\u68c0\u67e5\u8def\u5f84", (Object[])new Object[0]);
            throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_BASEMAP_NULL);
        }
        Captcha captcha = this.pictureTemplatesCut(originalImage, jigsawImage, jigsawImageBase64);
        if (captcha == null || StrUtil.isBlank((CharSequence)captcha.getJigsawImageBase64()) || StrUtil.isBlank((CharSequence)captcha.getOriginalImageBase64())) {
            throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_ERROR);
        }
        return captcha;
    }

    @Override
    public Captcha check(Captcha captcha) {
        String value;
        Point point1;
        String pointJson;
        Point point;
        this.check(captcha);
        String codeKey = String.format(REDIS_CAPTCHA_KEY, captcha.getToken());
        if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
            throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_INVALID);
        }
        String s = CaptchaServiceFactory.getCache(cacheType).get(codeKey);
        CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
        try {
            point = (Point)JacksonUtils.toObject((String)s, Point.class);
            assert (point != null);
            pointJson = BlockPuzzleCaptchaServiceImpl.decrypt(captcha.getPointJson(), point.getSecretKey());
            point1 = (Point)JacksonUtils.toObject((String)pointJson, Point.class);
        }
        catch (Exception e) {
            LogUtils.error((String)"\u9a8c\u8bc1\u7801\u5750\u6807\u89e3\u6790\u5931\u8d25", (Object[])new Object[]{e});
            this.afterValidateFail(captcha);
            throw new CaptchaException(e.getMessage());
        }
        assert (point1 != null);
        if (point.x - Integer.parseInt(slipOffset) > point1.x || point1.x > point.x + Integer.parseInt(slipOffset) || point.y != point1.y) {
            this.afterValidateFail(captcha);
            throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_COORDINATE_ERROR);
        }
        String secretKey = point.getSecretKey();
        try {
            value = Base64.getEncoder().encodeToString(AESUtils.encrypt((String)captcha.getToken().concat("---").concat(pointJson), (String)secretKey));
        }
        catch (Exception e) {
            LogUtils.error((String)"AES\u52a0\u5bc6\u5931\u8d25", (Object[])new Object[]{e});
            this.afterValidateFail(captcha);
            throw new CaptchaException(e.getMessage());
        }
        String secondKey = String.format(REDIS_SECOND_CAPTCHA_KEY, value);
        CaptchaServiceFactory.getCache(cacheType).set(secondKey, captcha.getToken(), EXPIRESIN_THREE);
        captcha.setResult(true);
        captcha.resetClientFlag();
        return captcha;
    }

    @Override
    public Captcha verification(Captcha captcha) {
        super.verification(captcha);
        try {
            String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY, captcha.getCaptchaVerification());
            if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
                throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_INVALID);
            }
            CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
        }
        catch (Exception e) {
            LogUtils.error((String)"\u9a8c\u8bc1\u7801\u5750\u6807\u89e3\u6790\u5931\u8d25", (Object[])new Object[]{e});
            throw new CaptchaException(e.getMessage());
        }
        return captcha;
    }

    public Captcha pictureTemplatesCut(BufferedImage originalImage, BufferedImage jigsawImage, String jigsawImageBase64) {
        try {
            Captcha dataVO = new Captcha();
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int jigsawWidth = jigsawImage.getWidth();
            int jigsawHeight = jigsawImage.getHeight();
            Point point = BlockPuzzleCaptchaServiceImpl.generateJigsawPoint(originalWidth, originalHeight, jigsawWidth, jigsawHeight);
            int x = point.getX();
            int y = point.getY();
            BufferedImage newJigsawImage = new BufferedImage(jigsawWidth, jigsawHeight, jigsawImage.getType());
            Graphics2D graphics = newJigsawImage.createGraphics();
            int bold = 5;
            newJigsawImage = graphics.getDeviceConfiguration().createCompatibleImage(jigsawWidth, jigsawHeight, 3);
            BlockPuzzleCaptchaServiceImpl.cutByTemplate(originalImage, jigsawImage, newJigsawImage, x, 0);
            if (captchaInterferenceOptions > 0) {
                String s;
                int position = 0;
                position = originalWidth - x - 5 > jigsawWidth * 2 ? RandomUtils.randomInt((int)(x + jigsawWidth + 5), (int)(originalWidth - jigsawWidth)) : RandomUtils.randomInt((int)100, (int)(x - jigsawWidth - 5));
                while (jigsawImageBase64.equals(s = ImageUtils.getslidingBlock())) {
                }
                BlockPuzzleCaptchaServiceImpl.interferenceByTemplate(originalImage, Objects.requireNonNull(ImageUtils.getBase64StrToImage(s)), position, 0);
            }
            if (captchaInterferenceOptions > 1) {
                String s;
                while (jigsawImageBase64.equals(s = ImageUtils.getslidingBlock())) {
                }
                int randomInt = RandomUtils.randomInt((int)jigsawWidth, (int)(100 - jigsawWidth));
                BlockPuzzleCaptchaServiceImpl.interferenceByTemplate(originalImage, Objects.requireNonNull(ImageUtils.getBase64StrToImage(s)), randomInt, 0);
            }
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setStroke(new BasicStroke(bold, 0, 2));
            graphics.drawImage((Image)newJigsawImage, 0, 0, null);
            graphics.dispose();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)newJigsawImage, "png", os);
            byte[] jigsawImages = os.toByteArray();
            ByteArrayOutputStream oriImagesOs = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)originalImage, "png", oriImagesOs);
            byte[] oriCopyImages = oriImagesOs.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            dataVO.setOriginalImageBase64(encoder.encodeToString(oriCopyImages).replaceAll("\r|\n", ""));
            dataVO.setJigsawImageBase64(encoder.encodeToString(jigsawImages).replaceAll("\r|\n", ""));
            dataVO.setToken(RandomUtils.randomString((int)16));
            dataVO.setSecretKey(point.getSecretKey());
            String codeKey = String.format(REDIS_CAPTCHA_KEY, dataVO.getToken());
            CaptchaServiceFactory.getCache(cacheType).set(codeKey, JacksonUtils.toJSONString((Object)point), EXPIRESIN_SECONDS);
            LogUtils.info((String)"token\uff1a{},point:{}", (Object[])new Object[]{dataVO.getToken(), JacksonUtils.toJSONString((Object)point)});
            return dataVO;
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e);
            return null;
        }
    }

    private static Point generateJigsawPoint(int originalWidth, int originalHeight, int jigsawWidth, int jigsawHeight) {
        Random random = new Random();
        int widthDifference = originalWidth - jigsawWidth;
        int heightDifference = originalHeight - jigsawHeight;
        int x = widthDifference <= 0 ? 5 : random.nextInt(originalWidth - jigsawWidth - 100) + 100;
        int y = heightDifference <= 0 ? 5 : random.nextInt(originalHeight - jigsawHeight) + 5;
        String key = null;
        if (captchaAesStatus.booleanValue()) {
            key = RandomUtils.randomString((int)16);
        }
        return new Point(x, y, key);
    }

    private static void cutByTemplate(BufferedImage oriImage, BufferedImage templateImage, BufferedImage newImage, int x, int y) {
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
        int xLength = templateImage.getWidth();
        int yLength = templateImage.getHeight();
        for (int i = 0; i < xLength; ++i) {
            for (int j = 0; j < yLength; ++j) {
                int rgb = templateImage.getRGB(i, j);
                if (rgb < 0) {
                    newImage.setRGB(i, j, oriImage.getRGB(x + i, y + j));
                    BlockPuzzleCaptchaServiceImpl.readPixel(oriImage, x + i, y + j, values);
                    BlockPuzzleCaptchaServiceImpl.fillMatrix(martrix, values);
                    oriImage.setRGB(x + i, y + j, BlockPuzzleCaptchaServiceImpl.avgMatrix(martrix));
                }
                if (i == xLength - 1 || j == yLength - 1) continue;
                int rightRgb = templateImage.getRGB(i + 1, j);
                int downRgb = templateImage.getRGB(i, j + 1);
                if (!(rgb >= 0 && rightRgb < 0 || rgb < 0 && rightRgb >= 0 || rgb >= 0 && downRgb < 0) && (rgb >= 0 || downRgb < 0)) continue;
                newImage.setRGB(i, j, Color.white.getRGB());
                oriImage.setRGB(x + i, y + j, Color.white.getRGB());
            }
        }
    }

    private static void interferenceByTemplate(BufferedImage oriImage, BufferedImage templateImage, int x, int y) {
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
        int xLength = templateImage.getWidth();
        int yLength = templateImage.getHeight();
        for (int i = 0; i < xLength; ++i) {
            for (int j = 0; j < yLength; ++j) {
                int rgb = templateImage.getRGB(i, j);
                if (rgb < 0) {
                    BlockPuzzleCaptchaServiceImpl.readPixel(oriImage, x + i, y + j, values);
                    BlockPuzzleCaptchaServiceImpl.fillMatrix(martrix, values);
                    oriImage.setRGB(x + i, y + j, BlockPuzzleCaptchaServiceImpl.avgMatrix(martrix));
                }
                if (i == xLength - 1 || j == yLength - 1) continue;
                int rightRgb = templateImage.getRGB(i + 1, j);
                int downRgb = templateImage.getRGB(i, j + 1);
                if (!(rgb >= 0 && rightRgb < 0 || rgb < 0 && rightRgb >= 0 || rgb >= 0 && downRgb < 0) && (rgb >= 0 || downRgb < 0)) continue;
                oriImage.setRGB(x + i, y + j, Color.white.getRGB());
            }
        }
    }

    private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;
        for (int i = xStart; i < 3 + xStart; ++i) {
            for (int j = yStart; j < 3 + yStart; ++j) {
                int tx = i;
                if (tx < 0) {
                    tx = -tx;
                } else if (tx >= img.getWidth()) {
                    tx = x;
                }
                int ty = j;
                if (ty < 0) {
                    ty = -ty;
                } else if (ty >= img.getHeight()) {
                    ty = y;
                }
                pixels[current++] = img.getRGB(tx, ty);
            }
        }
    }

    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;
        for (int i = 0; i < matrix.length; ++i) {
            int[] x = matrix[i];
            for (int j = 0; j < x.length; ++j) {
                x[j] = values[filled++];
            }
        }
    }

    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int[] x : matrix) {
            for (int j = 0; j < x.length; ++j) {
                if (j == 1) continue;
                Color c = new Color(x[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        return new Color(r / 8, g / 8, b / 8).getRGB();
    }
}

