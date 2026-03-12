package com.kuma.boot.captcha.support.behavior.renderer;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.IdUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.behavior.definition.AbstractBehaviorRenderer;
import com.kuma.boot.captcha.support.behavior.dto.JigsawCaptcha;
import com.kuma.boot.captcha.support.core.algorithm.GaussianBlur;
import com.kuma.boot.captcha.support.core.definition.domain.Coordinate;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCategory;
import com.kuma.boot.captcha.support.core.dto.Captcha;
import com.kuma.boot.captcha.support.core.dto.Verification;
import com.kuma.boot.captcha.support.core.exception.CaptchaHasExpiredException;
import com.kuma.boot.captcha.support.core.exception.CaptchaMismatchException;
import com.kuma.boot.captcha.support.core.exception.CaptchaParameterIllegalException;
import com.kuma.boot.captcha.support.core.provider.RandomProvider;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.time.Duration;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JigsawCaptchaRenderer extends AbstractBehaviorRenderer {
    private static final Logger log = LoggerFactory.getLogger(JigsawCaptchaRenderer.class);
    private static final int AREA_SIZE = 3;
    private static final int AREA_ARRAY_SIZE = 9;
    private static final int BOLD = 5;
    private static final int OFFSET = 100;
    private JigsawCaptcha jigsawCaptcha;

    public JigsawCaptchaRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public JigsawCaptchaRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    public String getCategory() {
        return CaptchaCategory.JIGSAW.getConstant();
    }

    public Captcha getCapcha(String key) {
        String identity = key;
        if (StringUtils.isBlank(key)) {
            identity = IdUtil.fastUUID();
        }

        this.create(identity);
        return this.jigsawCaptcha;
    }

    public Coordinate nextStamp(String key) {
        Metadata metadata = this.draw();
        JigsawCaptcha jigsawCaptcha = new JigsawCaptcha();
        jigsawCaptcha.setIdentity(key);
        jigsawCaptcha.setOriginalImageBase64(metadata.getOriginalImageBase64());
        jigsawCaptcha.setSliderImageBase64(metadata.getSliderImageBase64());
        this.jigsawCaptcha = jigsawCaptcha;
        return metadata.getCoordinate();
    }

    public boolean verify(Verification verification) {
        if (!ObjectUtils.isEmpty(verification) && !ObjectUtils.isEmpty(verification.getCoordinate())) {
            Coordinate store = (Coordinate)this.get(verification.getIdentity());
            if (ObjectUtils.isEmpty(store)) {
                throw new CaptchaHasExpiredException("Stamp is invalid!");
            } else {
                this.delete(verification.getIdentity());
                Coordinate real = verification.getCoordinate();
                if (!this.isDeflected(real.getX(), store.getX(), this.getCaptchaProperties().getJigsaw().getDeviation()) && real.getY() == store.getY()) {
                    return true;
                } else {
                    throw new CaptchaMismatchException();
                }
            }
        } else {
            throw new CaptchaParameterIllegalException("Parameter Stamp value is null");
        }
    }

    public Metadata draw() {
        BufferedImage originalImage = this.getResourceProvider().getRandomOriginalImage();
        Graphics backgroundGraphics = originalImage.getGraphics();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        this.addWatermark(backgroundGraphics, width, height);
        String sliderImageBase64 = this.getResourceProvider().getRandomBase64TemplateImage();
        BufferedImage templateImage = ImgUtil.toImage(sliderImageBase64);
        return this.draw(originalImage, templateImage, sliderImageBase64);
    }

    private Metadata draw(BufferedImage originalImage, BufferedImage templateImage, String sliderImageBase64) {
        int originalImageWidth = originalImage.getWidth();
        int originalImageHeight = originalImage.getHeight();
        int templateImageWidth = templateImage.getWidth();
        int templateImageHeight = templateImage.getHeight();
        log.trace("[kmc] |- Jigsaw captcha original image width is [{}], height is [{}].", originalImageWidth, originalImageHeight);
        log.trace("[kmc] |- Jigsaw captcha template image width is [{}], height is [{}].", templateImageWidth, templateImageHeight);
        Coordinate coordinate = this.createImageMattingCoordinate(originalImageWidth, originalImageHeight, templateImageWidth, templateImageHeight);
        int x = coordinate.getX();
        int y = coordinate.getY();
        BufferedImage jigsawImage = new BufferedImage(templateImageWidth, templateImageHeight, templateImage.getType());
        Graphics2D graphics = jigsawImage.createGraphics();
        jigsawImage = graphics.getDeviceConfiguration().createCompatibleImage(templateImageWidth, templateImageHeight, 3);
        this.mattingByTemplate(originalImage, templateImage, jigsawImage, x, 0);
        int interferencePosition = this.createInterferencePosition(originalImageWidth, templateImageWidth, x);
        if (interferencePosition != 0) {
            this.addInterference(originalImage, sliderImageBase64, interferencePosition);
        }

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(5.0F, 0, 2));
        graphics.drawImage(jigsawImage, 0, 0, (ImageObserver)null);
        graphics.dispose();
        log.trace("[kmc] |- Jigsaw captcha jigsaw image width is [{}], height is [{}].", jigsawImage.getWidth(), jigsawImage.getHeight());
        Metadata metadata = new Metadata();
        metadata.setOriginalImageBase64(this.toBase64(originalImage));
        metadata.setSliderImageBase64(this.toBase64(jigsawImage));
        metadata.setCoordinate(coordinate);
        return metadata;
    }

    private Coordinate createImageMattingCoordinate(int originalImageWidth, int originalImageHeight, int templateImageWidth, int templateImageHeight) {
        int availableWidth = originalImageWidth - templateImageWidth;
        int availableHeight = originalImageHeight - templateImageHeight;
        int x = 5;
        int y = 5;
        if (availableWidth > 0) {
            x = RandomProvider.randomInt(availableWidth - 100) + 100;
        }

        if (availableHeight > 0) {
            y = RandomProvider.randomInt(availableHeight) + 5;
        }

        log.debug("[kmc] |- Jigsaw captcha image matting coordinate is x: [{}], y: [{}].", x, y);
        return new Coordinate(x, y);
    }

    private void mattingByTemplate(BufferedImage originalImage, BufferedImage templateImage, BufferedImage jigsawImage, int x, int y) {
        int[][] matrix = new int[3][3];
        int[] values = new int[9];
        int templateImageWidth = templateImage.getWidth();
        int templateImageHeight = templateImage.getHeight();

        for(int i = 0; i < templateImageWidth; ++i) {
            for(int j = 0; j < templateImageHeight; ++j) {
                int pixelX = x + i;
                int pixelY = y + j;
                int templateImageRgb = this.getImageRgb(templateImage, i, j);
                if (templateImageRgb < 0) {
                    jigsawImage.setRGB(i, j, this.getImageRgb(originalImage, pixelX, pixelY));
                    GaussianBlur.execute(originalImage, pixelX, pixelY, matrix, values, 3);
                }

                if (!this.isOutOfBound(i, j, templateImageWidth, templateImageHeight) && this.isCritical(templateImage, i, j, templateImageRgb)) {
                    jigsawImage.setRGB(i, j, Color.white.getRGB());
                    originalImage.setRGB(pixelX, pixelY, Color.white.getRGB());
                }
            }
        }

    }

    private int getImageRgb(BufferedImage bufferedImage, int i, int j) {
        return bufferedImage.getRGB(i, j);
    }

    private int getTemplateImageRightBorderRgb(BufferedImage templateImage, int i, int j) {
        return this.getImageRgb(templateImage, i + 1, j);
    }

    private int getTemplateImageBottomBorderRgb(BufferedImage templateImage, int i, int j) {
        return this.getImageRgb(templateImage, i, j + 1);
    }

    private boolean isOutOfBound(int x, int y, int templateImageWidth, int templateImageHeight) {
        return x == templateImageWidth - 1 || y == templateImageHeight - 1;
    }

    private boolean isPixelBoundary(int main, int boarder) {
        return main < 0 && boarder >= 0;
    }

    private boolean isNoPixelBoundary(int main, int boarder) {
        return main >= 0 && boarder < 0;
    }

    private boolean isBoundary(int main, int boarder) {
        return this.isNoPixelBoundary(main, boarder) || this.isPixelBoundary(main, boarder);
    }

    private boolean isCritical(BufferedImage templateImage, int x, int y, int baseRgb) {
        int rightBorderRgb = this.getTemplateImageRightBorderRgb(templateImage, x, y);
        int bottomBorderRgb = this.getTemplateImageBottomBorderRgb(templateImage, x, y);
        return this.isBoundary(baseRgb, rightBorderRgb) || this.isBoundary(baseRgb, bottomBorderRgb);
    }

    private int createInterferencePosition(int originalImageWidth, int templateImageWidth, int x) {
        int interferenceOptions = this.getCaptchaProperties().getJigsaw().getInterference();
        int position = 0;
        if (interferenceOptions > 0) {
            if (originalImageWidth - x - 5 > templateImageWidth * 2) {
                position = RandomProvider.randomInt(x + templateImageWidth + 5, originalImageWidth - templateImageWidth);
            } else {
                position = RandomProvider.randomInt(100, x - templateImageWidth - 5);
            }
        }

        if (interferenceOptions > 1) {
            position = RandomProvider.randomInt(templateImageWidth, 100 - templateImageWidth);
        }

        return position;
    }

    private void addInterference(BufferedImage originalImage, String sliderImageBase64, int position) {
        String data;
        do {
            data = this.getResourceProvider().getRandomBase64TemplateImage();
        } while(sliderImageBase64.equals(data));

        this.interferenceByTemplate(originalImage, (BufferedImage)Objects.requireNonNull(ImgUtil.toImage(data)), position, 0);
    }

    private void interferenceByTemplate(BufferedImage originalImage, BufferedImage templateImage, int x, int y) {
        int[][] matrix = new int[3][3];
        int[] values = new int[9];
        int templateImageWidth = templateImage.getWidth();
        int templateImageHeight = templateImage.getHeight();

        for(int i = 0; i < templateImageWidth; ++i) {
            for(int j = 0; j < templateImageHeight; ++j) {
                int pixelX = x + i;
                int pixelY = y + j;
                int templateImageRgb = this.getImageRgb(templateImage, i, j);
                if (templateImageRgb < 0) {
                    GaussianBlur.execute(originalImage, pixelX, pixelY, matrix, values, 3);
                }

                if (!this.isOutOfBound(i, j, templateImageWidth, templateImageHeight) && this.isCritical(templateImage, i, j, templateImageRgb)) {
                    originalImage.setRGB(pixelX, pixelY, Color.white.getRGB());
                }
            }
        }

    }
}
