//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;

public class BlockPuzzleCaptchaServiceImpl extends AbstractCaptchaService {
    public void init(Properties config) {
        super.init(config);
    }

    public void destroy(Properties config) {
        LogUtils.info("start-clear-history-data-}", new Object[]{this.captchaType()});
    }

    public String captchaType() {
        return CaptchaTypeEnum.BLOCKPUZZLE.getCodeValue();
    }

    public Captcha get(Captcha captchaVO) {
        super.get(captchaVO);
        BufferedImage originalImage = ImageUtils.getOriginal();
        if (null == originalImage) {
            LogUtils.error("滑动底图未初始化成功，请检查路径", new Object[0]);
            throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_BASEMAP_NULL);
        } else {
            Graphics backgroundGraphics = originalImage.getGraphics();
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            backgroundGraphics.setFont(this.waterMarkFont);
            backgroundGraphics.setColor(Color.white);
            backgroundGraphics.drawString(waterMark, width - getEnOrChLength(waterMark), height - HAN_ZI_SIZE / 2 + 7);
            String jigsawImageBase64 = ImageUtils.getslidingBlock();
            BufferedImage jigsawImage = ImageUtils.getBase64StrToImage(jigsawImageBase64);
            if (null == jigsawImage) {
                LogUtils.error("滑动底图未初始化成功，请检查路径", new Object[0]);
                throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_BASEMAP_NULL);
            } else {
                Captcha captcha = this.pictureTemplatesCut(originalImage, jigsawImage, jigsawImageBase64);
                if (captcha != null && !StrUtil.isBlank(captcha.getJigsawImageBase64()) && !StrUtil.isBlank(captcha.getOriginalImageBase64())) {
                    return captcha;
                } else {
                    throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_ERROR);
                }
            }
        }
    }

    public Captcha check(Captcha captcha) {
        this.check(captcha);
        String codeKey = String.format(REDIS_CAPTCHA_KEY, captcha.getToken());
        if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
            throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_INVALID);
        } else {
            String s = CaptchaServiceFactory.getCache(cacheType).get(codeKey);
            CaptchaServiceFactory.getCache(cacheType).delete(codeKey);

            Point point;
            Point point1;
            String pointJson;
            try {
                point = (Point)JacksonUtils.toObject(s, Point.class);

                assert point != null;

                pointJson = decrypt(captcha.getPointJson(), point.getSecretKey());
                point1 = (Point)JacksonUtils.toObject(pointJson, Point.class);
            } catch (Exception e) {
                LogUtils.error("验证码坐标解析失败", new Object[]{e});
                this.afterValidateFail(captcha);
                throw new CaptchaException(e.getMessage());
            }

            assert point1 != null;

            if (point.x - Integer.parseInt(slipOffset) <= point1.x && point1.x <= point.x + Integer.parseInt(slipOffset) && point.y == point1.y) {
                String secretKey = point.getSecretKey();

                String value;
                try {
                    value = Base64.getEncoder().encodeToString(AESUtils.encrypt(captcha.getToken().concat("---").concat(pointJson), secretKey));
                } catch (Exception e) {
                    LogUtils.error("AES加密失败", new Object[]{e});
                    this.afterValidateFail(captcha);
                    throw new CaptchaException(e.getMessage());
                }

                String secondKey = String.format(REDIS_SECOND_CAPTCHA_KEY, value);
                CaptchaServiceFactory.getCache(cacheType).set(secondKey, captcha.getToken(), EXPIRESIN_THREE);
                captcha.setResult(true);
                captcha.resetClientFlag();
                return captcha;
            } else {
                this.afterValidateFail(captcha);
                throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_COORDINATE_ERROR);
            }
        }
    }

    public Captcha verification(Captcha captcha) {
        super.verification(captcha);

        try {
            String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY, captcha.getCaptchaVerification());
            if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
                throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_INVALID);
            } else {
                CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
                return captcha;
            }
        } catch (Exception e) {
            LogUtils.error("验证码坐标解析失败", new Object[]{e});
            throw new CaptchaException(e.getMessage());
        }
    }

    public Captcha pictureTemplatesCut(BufferedImage originalImage, BufferedImage jigsawImage, String jigsawImageBase64) {
        try {
            Captcha dataVO = new Captcha();
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int jigsawWidth = jigsawImage.getWidth();
            int jigsawHeight = jigsawImage.getHeight();
            Point point = generateJigsawPoint(originalWidth, originalHeight, jigsawWidth, jigsawHeight);
            int x = point.getX();
            int y = point.getY();
            BufferedImage newJigsawImage = new BufferedImage(jigsawWidth, jigsawHeight, jigsawImage.getType());
            Graphics2D graphics = newJigsawImage.createGraphics();
            int bold = 5;
            newJigsawImage = graphics.getDeviceConfiguration().createCompatibleImage(jigsawWidth, jigsawHeight, 3);
            cutByTemplate(originalImage, jigsawImage, newJigsawImage, x, 0);
            if (captchaInterferenceOptions > 0) {
                int position = 0;
                if (originalWidth - x - 5 > jigsawWidth * 2) {
                    position = RandomUtils.randomInt(x + jigsawWidth + 5, originalWidth - jigsawWidth);
                } else {
                    position = RandomUtils.randomInt(100, x - jigsawWidth - 5);
                }

                String s;
                do {
                    s = ImageUtils.getslidingBlock();
                } while(jigsawImageBase64.equals(s));

                interferenceByTemplate(originalImage, (BufferedImage)Objects.requireNonNull(ImageUtils.getBase64StrToImage(s)), position, 0);
            }

            if (captchaInterferenceOptions > 1) {
                String s;
                do {
                    s = ImageUtils.getslidingBlock();
                } while(jigsawImageBase64.equals(s));

                int randomInt = RandomUtils.randomInt(jigsawWidth, 100 - jigsawWidth);
                interferenceByTemplate(originalImage, (BufferedImage)Objects.requireNonNull(ImageUtils.getBase64StrToImage(s)), randomInt, 0);
            }

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setStroke(new BasicStroke((float)bold, 0, 2));
            graphics.drawImage(newJigsawImage, 0, 0, (ImageObserver)null);
            graphics.dispose();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(newJigsawImage, "png", os);
            byte[] jigsawImages = os.toByteArray();
            ByteArrayOutputStream oriImagesOs = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "png", oriImagesOs);
            byte[] oriCopyImages = oriImagesOs.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            dataVO.setOriginalImageBase64(encoder.encodeToString(oriCopyImages).replaceAll("\r|\n", ""));
            dataVO.setJigsawImageBase64(encoder.encodeToString(jigsawImages).replaceAll("\r|\n", ""));
            dataVO.setToken(RandomUtils.randomString(16));
            dataVO.setSecretKey(point.getSecretKey());
            String codeKey = String.format(REDIS_CAPTCHA_KEY, dataVO.getToken());
            CaptchaServiceFactory.getCache(cacheType).set(codeKey, JacksonUtils.toJSONString(point), EXPIRESIN_SECONDS);
            LogUtils.info("token：{},point:{}", new Object[]{dataVO.getToken(), JacksonUtils.toJSONString(point)});
            return dataVO;
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    private static Point generateJigsawPoint(int originalWidth, int originalHeight, int jigsawWidth, int jigsawHeight) {
        Random random = new Random();
        int widthDifference = originalWidth - jigsawWidth;
        int heightDifference = originalHeight - jigsawHeight;
        int x;
        if (widthDifference <= 0) {
            x = 5;
        } else {
            x = random.nextInt(originalWidth - jigsawWidth - 100) + 100;
        }

        int y;
        if (heightDifference <= 0) {
            y = 5;
        } else {
            y = random.nextInt(originalHeight - jigsawHeight) + 5;
        }

        String key = null;
        if (captchaAesStatus) {
            key = RandomUtils.randomString(16);
        }

        return new Point(x, y, key);
    }

    private static void cutByTemplate(BufferedImage oriImage, BufferedImage templateImage, BufferedImage newImage, int x, int y) {
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
        int xLength = templateImage.getWidth();
        int yLength = templateImage.getHeight();

        for(int i = 0; i < xLength; ++i) {
            for(int j = 0; j < yLength; ++j) {
                int rgb = templateImage.getRGB(i, j);
                if (rgb < 0) {
                    newImage.setRGB(i, j, oriImage.getRGB(x + i, y + j));
                    readPixel(oriImage, x + i, y + j, values);
                    fillMatrix(martrix, values);
                    oriImage.setRGB(x + i, y + j, avgMatrix(martrix));
                }

                if (i != xLength - 1 && j != yLength - 1) {
                    int rightRgb = templateImage.getRGB(i + 1, j);
                    int downRgb = templateImage.getRGB(i, j + 1);
                    if (rgb >= 0 && rightRgb < 0 || rgb < 0 && rightRgb >= 0 || rgb >= 0 && downRgb < 0 || rgb < 0 && downRgb >= 0) {
                        newImage.setRGB(i, j, Color.white.getRGB());
                        oriImage.setRGB(x + i, y + j, Color.white.getRGB());
                    }
                }
            }
        }

    }

    private static void interferenceByTemplate(BufferedImage oriImage, BufferedImage templateImage, int x, int y) {
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
        int xLength = templateImage.getWidth();
        int yLength = templateImage.getHeight();

        for(int i = 0; i < xLength; ++i) {
            for(int j = 0; j < yLength; ++j) {
                int rgb = templateImage.getRGB(i, j);
                if (rgb < 0) {
                    readPixel(oriImage, x + i, y + j, values);
                    fillMatrix(martrix, values);
                    oriImage.setRGB(x + i, y + j, avgMatrix(martrix));
                }

                if (i != xLength - 1 && j != yLength - 1) {
                    int rightRgb = templateImage.getRGB(i + 1, j);
                    int downRgb = templateImage.getRGB(i, j + 1);
                    if (rgb >= 0 && rightRgb < 0 || rgb < 0 && rightRgb >= 0 || rgb >= 0 && downRgb < 0 || rgb < 0 && downRgb >= 0) {
                        oriImage.setRGB(x + i, y + j, Color.white.getRGB());
                    }
                }
            }
        }

    }

    private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;

        for(int i = xStart; i < 3 + xStart; ++i) {
            for(int j = yStart; j < 3 + yStart; ++j) {
                int tx = i;
                if (i < 0) {
                    tx = -i;
                } else if (i >= img.getWidth()) {
                    tx = x;
                }

                int ty = j;
                if (j < 0) {
                    ty = -j;
                } else if (j >= img.getHeight()) {
                    ty = y;
                }

                pixels[current++] = img.getRGB(tx, ty);
            }
        }

    }

    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;

        for(int i = 0; i < matrix.length; ++i) {
            int[] x = matrix[i];

            for(int j = 0; j < x.length; ++j) {
                x[j] = values[filled++];
            }
        }

    }

    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;

        for(int[] x : matrix) {
            for(int j = 0; j < x.length; ++j) {
                if (j != 1) {
                    Color c = new Color(x[j]);
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                }
            }
        }

        return (new Color(r / 8, g / 8, b / 8)).getRGB();
    }
}
