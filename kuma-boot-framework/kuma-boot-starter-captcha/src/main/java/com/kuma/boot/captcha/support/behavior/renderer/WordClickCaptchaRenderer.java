package com.kuma.boot.captcha.support.behavior.renderer;

import cn.hutool.core.util.IdUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.behavior.definition.AbstractBehaviorRenderer;
import com.kuma.boot.captcha.support.behavior.dto.WordClickCaptcha;
import com.kuma.boot.captcha.support.core.definition.domain.Coordinate;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCategory;
import com.kuma.boot.captcha.support.core.definition.enums.FontStyle;
import com.kuma.boot.captcha.support.core.dto.Captcha;
import com.kuma.boot.captcha.support.core.dto.Verification;
import com.kuma.boot.captcha.support.core.exception.CaptchaHasExpiredException;
import com.kuma.boot.captcha.support.core.exception.CaptchaMismatchException;
import com.kuma.boot.captcha.support.core.exception.CaptchaParameterIllegalException;
import com.kuma.boot.captcha.support.core.provider.RandomProvider;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;

public class WordClickCaptchaRenderer extends AbstractBehaviorRenderer {
    private WordClickCaptcha wordClickCaptcha;

    public WordClickCaptchaRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public WordClickCaptchaRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    private Font getFont() {
        int fontSize = this.getCaptchaProperties().getWordClick().getFontSize();
        String fontName = this.getCaptchaProperties().getWordClick().getFontName();
        FontStyle fontStyle = this.getCaptchaProperties().getWordClick().getFontStyle();
        return this.getResourceProvider().getFont(fontName, fontSize, fontStyle);
    }

    public String getCategory() {
        return CaptchaCategory.WORD_CLICK.getConstant();
    }

    public List<Coordinate> nextStamp(String key) {
        Metadata metadata = this.draw();
        WordClickObfuscator wordClickObfuscator = new WordClickObfuscator(metadata.getWords(), metadata.getCoordinates());
        WordClickCaptcha wordClickCaptcha = new WordClickCaptcha();
        wordClickCaptcha.setIdentity(key);
        wordClickCaptcha.setWordClickImageBase64(metadata.getWordClickImageBase64());
        wordClickCaptcha.setWords(wordClickObfuscator.getWordString());
        wordClickCaptcha.setWordsCount(metadata.getWords().size());
        this.wordClickCaptcha = wordClickCaptcha;
        return wordClickObfuscator.getCoordinates();
    }

    public Captcha getCapcha(String key) {
        String identity = key;
        if (StringUtils.isBlank(key)) {
            identity = IdUtil.fastUUID();
        }

        this.create(identity);
        return this.wordClickCaptcha;
    }

    @SuppressWarnings("unchecked")
    public boolean verify(Verification verification) {
        if (!ObjectUtils.isEmpty(verification) && !CollectionUtils.isEmpty(verification.getCoordinates())) {
            List<Coordinate> store = (List<Coordinate>)this.get(verification.getIdentity());
            if (CollectionUtils.isEmpty(store)) {
                throw new CaptchaHasExpiredException("Stamp is invalid!");
            } else {
                this.delete(verification.getIdentity());
                List<Coordinate> real = verification.getCoordinates();

                for(int i = 0; i < store.size(); ++i) {
                    if (this.isDeflected(((Coordinate)real.get(i)).getX(), ((Coordinate)store.get(i)).getX(), this.getFontSize()) || this.isDeflected(((Coordinate)real.get(i)).getY(), ((Coordinate)store.get(i)).getY(), this.getFontSize())) {
                        throw new CaptchaMismatchException();
                    }
                }

                return true;
            }
        } else {
            throw new CaptchaParameterIllegalException("Parameter Stamp value is null");
        }
    }

    public Metadata draw() {
        BufferedImage backgroundImage = this.getResourceProvider().getRandomWordClickImage();
        int wordCount = this.getCaptchaProperties().getWordClick().getWordCount();
        List<String> words = RandomProvider.randomWords(wordCount);
        Graphics backgroundGraphics = backgroundImage.getGraphics();
        int backgroundImageWidth = backgroundImage.getWidth();
        int backgroundImageHeight = backgroundImage.getHeight();
        @SuppressWarnings("unchecked")
        List<Coordinate> coordinates = (List<Coordinate>)IntStream.range(0, words.size()).mapToObj((index) -> this.drawWord(backgroundGraphics, backgroundImageWidth, backgroundImageHeight, index, wordCount, (String)words.get(index))).collect(Collectors.toList());
        this.addWatermark(backgroundGraphics, backgroundImageWidth, backgroundImageHeight);
        BufferedImage combinedImage = new BufferedImage(backgroundImageWidth, backgroundImageHeight, 1);
        Graphics combinedGraphics = combinedImage.getGraphics();
        combinedGraphics.drawImage(backgroundImage, 0, 0, (ImageObserver)null);
        int excludeWordIndex = RandomProvider.randomInt(1, wordCount) - 1;
        words.remove(excludeWordIndex);
        coordinates.remove(excludeWordIndex);
        Metadata metadata = new Metadata();
        metadata.setWordClickImageBase64(this.toBase64(backgroundImage));
        metadata.setCoordinates(coordinates);
        metadata.setWords(words);
        return metadata;
    }

    private Coordinate drawWord(Graphics graphics, int width, int height, int index, int wordCount, String word) {
        Coordinate coordinate = this.randomWordCoordinate(width, height, index, wordCount);
        if (this.getCaptchaProperties().getWordClick().isRandomColor()) {
            graphics.setColor(new Color(RandomProvider.randomInt(1, 255), RandomProvider.randomInt(1, 255), RandomProvider.randomInt(1, 255)));
        } else {
            graphics.setColor(Color.BLACK);
        }

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians((double)RandomProvider.randomInt(-45, 45)), (double)0.0F, (double)0.0F);
        Font rotatedFont = this.getFont().deriveFont(affineTransform);
        graphics.setFont(rotatedFont);
        graphics.drawString(word, coordinate.getX(), coordinate.getY());
        return coordinate;
    }

    private int getFontSize() {
        return this.getCaptchaProperties().getWordClick().getFontSize();
    }

    private int getHalfFontSize() {
        return this.getFontSize() / 2;
    }

    private Coordinate randomWordCoordinate(int backgroundImageWidth, int backgroundImageHeight, int wordIndex, int wordCount) {
        int wordSize = this.getFontSize();
        int halfWordSize = this.getHalfFontSize();
        int averageWidth = backgroundImageWidth / (wordCount + 1);
        int x;
        if (averageWidth < halfWordSize) {
            x = RandomProvider.randomInt(this.getStartInclusive(halfWordSize), backgroundImageWidth);
        } else if (wordIndex == 0) {
            x = RandomProvider.randomInt(this.getStartInclusive(halfWordSize), this.getEndExclusive(wordIndex, averageWidth, halfWordSize));
        } else {
            x = RandomProvider.randomInt(averageWidth * wordIndex + halfWordSize, this.getEndExclusive(wordIndex, averageWidth, halfWordSize));
        }

        int y = RandomProvider.randomInt(wordSize, backgroundImageHeight - wordSize);
        return new Coordinate(x, y);
    }

    private int getStartInclusive(int halfWordSize) {
        return 1 + halfWordSize;
    }

    private int getEndExclusive(int wordIndex, int averageWidth, int halfWordSize) {
        return averageWidth * (wordIndex + 1) - halfWordSize;
    }
}
