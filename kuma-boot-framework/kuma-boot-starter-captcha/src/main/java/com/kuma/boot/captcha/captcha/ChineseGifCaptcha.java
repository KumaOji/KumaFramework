/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.captcha.captcha;

import com.kuma.boot.common.utils.log.LogUtils;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class ChineseGifCaptcha
extends AbstractChineseCaptcha {
    public ChineseGifCaptcha() {
    }

    public ChineseGifCaptcha(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    public ChineseGifCaptcha(int width, int height, int len) {
        this(width, height);
        this.setLen(len);
    }

    public ChineseGifCaptcha(int width, int height, int len, Font font) {
        this(width, height, len);
        this.setFont(font);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean out(OutputStream os) {
        boolean ok;
        this.checkAlpha();
        try {
            char[] rands = this.textChar();
            GifEncoder gifEncoder = new GifEncoder();
            gifEncoder.start(os);
            gifEncoder.setQuality(180);
            gifEncoder.setDelay(100);
            gifEncoder.setRepeat(0);
            Color fontcolor = this.color();
            for (int i = 0; i < this.len; ++i) {
                BufferedImage frame = this.graphicsImage(fontcolor, rands, i);
                gifEncoder.addFrame(frame);
                frame.flush();
            }
            gifEncoder.finish();
            ok = true;
        }
        finally {
            try {
                os.close();
            }
            catch (IOException e) {
                LogUtils.error((Throwable)e);
            }
        }
        return ok;
    }

    private BufferedImage graphicsImage(Color fontcolor, char[] strs, int flag) {
        AlphaComposite ac3;
        BufferedImage image = new BufferedImage(this.width, this.height, 1);
        Graphics2D g2d = (Graphics2D)image.getGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, this.width, this.height);
        g2d.setColor(fontcolor);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int hp = this.height - this.font.getSize() >> 1;
        int h = this.height - hp;
        int w = this.width / strs.length;
        int sp = (w - this.font.getSize()) / 2;
        for (int i = 0; i < this.len; ++i) {
            ac3 = AlphaComposite.getInstance(3, this.getAlpha(flag, i));
            g2d.setComposite(ac3);
            int x = i * w + sp + ChineseGifCaptcha.num(-3, 3);
            int y = h + ChineseGifCaptcha.num(-3, 3);
            if (x < 0) {
                x = 0;
            }
            if (x + this.font.getSize() > this.width) {
                x = this.width - this.font.getSize();
            }
            if (y > this.height) {
                y = this.height;
            }
            if (y - this.font.getSize() < 0) {
                y = this.font.getSize();
            }
            g2d.setFont(this.font.deriveFont(ChineseGifCaptcha.num(2) == 0 ? 0 : 2));
            g2d.drawString(String.valueOf(strs[i]), x, y);
        }
        g2d.setStroke(new BasicStroke(1.25f, 0, 2));
        ac3 = AlphaComposite.getInstance(3, 0.45f);
        g2d.setComposite(ac3);
        this.drawLine(1, g2d.getColor(), g2d);
        this.drawOval(3, g2d.getColor(), g2d);
        g2d.dispose();
        return image;
    }

    private float getAlpha(int i, int j) {
        int num = i + j;
        float r = 1.0f / (float)(this.len - 1);
        float s = (float)this.len * r;
        return num >= this.len ? (float)num * r - s : (float)num * r;
    }
}

