//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.behavior.definition;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.AbstractRenderer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public abstract class AbstractBehaviorRenderer extends AbstractRenderer {
    public AbstractBehaviorRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public AbstractBehaviorRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    protected int getEnOrZhLength(String s) {
        int enCount = 0;
        int zhCount = 0;

        for(int i = 0; i < s.length(); ++i) {
            int length = String.valueOf(s.charAt(i)).getBytes(StandardCharsets.UTF_8).length;
            if (length > 1) {
                ++zhCount;
            } else {
                ++enCount;
            }
        }

        int zhOffset = this.getHalfWatermarkFontSize() * zhCount + 5;
        int enOffset = enCount * 8;
        return zhOffset + enOffset;
    }

    private int getWatermarkFontSize() {
        return this.getCaptchaProperties().getWatermark().getFontSize();
    }

    private int getHalfWatermarkFontSize() {
        return this.getWatermarkFontSize() / 2;
    }

    protected void addWatermark(Graphics graphics, int width, int height) {
        int fontSize = this.getHalfWatermarkFontSize();
        Font watermakFont = this.getResourceProvider().getWaterMarkFont(fontSize);
        graphics.setFont(watermakFont);
        graphics.setColor(Color.white);
        String content = this.getCaptchaProperties().getWatermark().getContent();
        graphics.drawString(content, width - this.getEnOrZhLength(content), height - this.getHalfWatermarkFontSize() + 7);
    }

    protected boolean isUnderOffset(int actualValue, int standardValue, int threshold) {
        return actualValue < standardValue - threshold;
    }

    protected boolean isOverOffset(int actualValue, int standardValue, int threshold) {
        return actualValue > standardValue + threshold;
    }

    protected boolean isDeflected(int actualValue, int standardValue, int threshold) {
        return this.isUnderOffset(actualValue, standardValue, threshold) || this.isOverOffset(actualValue, standardValue, threshold);
    }
}
