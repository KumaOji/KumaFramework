//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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

public class GifCaptcha extends BaseCaptcha {
    public GifCaptcha() {
    }

    public GifCaptcha(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    public GifCaptcha(int width, int height, int len) {
        this(width, height);
        this.setLen(len);
    }

    public GifCaptcha(int width, int height, int len, Font font) {
        this(width, height, len);
        this.setFont(font);
    }

    public boolean out(OutputStream os) {
        this.checkAlpha();

        boolean ok;
        try {
            char[] rands = this.textChar();
            GifEncoder gifEncoder = new GifEncoder();
            gifEncoder.start(os);
            gifEncoder.setQuality(180);
            gifEncoder.setDelay(100);
            gifEncoder.setRepeat(0);
            Color[] fontcolor = new Color[this.len];

            for(int i = 0; i < this.len; ++i) {
                fontcolor[i] = new Color(20 + num(110), 20 + num(110), 20 + num(110));
            }

            for(int i = 0; i < this.len; ++i) {
                BufferedImage frame = this.graphicsImage(fontcolor, rands, i);
                gifEncoder.addFrame(frame);
                frame.flush();
            }

            gifEncoder.finish();
            ok = true;
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                LogUtils.error(e);
            }

        }

        return ok;
    }

    private BufferedImage graphicsImage(Color[] fontcolor, char[] strs, int flag) {
        BufferedImage image = new BufferedImage(this.width, this.height, 1);
        Graphics2D g2d = (Graphics2D)image.getGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, this.width, this.height);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(1.3F, 0, 2));
        this.drawOval(4, g2d);
        this.drawLine(2, g2d);
        int hp = this.height - this.font.getSize() >> 1;
        int h = this.height - hp;
        int w = this.width / strs.length;
        int sp = (w - this.font.getSize()) / 2;

        for(int i = 0; i < strs.length; ++i) {
            AlphaComposite ac3 = AlphaComposite.getInstance(3, this.getAlpha(flag, i));
            g2d.setComposite(ac3);
            g2d.setColor(fontcolor[i]);
            int x = i * w + sp + num(3);
            int y = h - num(3, 6);
            if (x < 8) {
                x = 8;
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

            g2d.setFont(this.font.deriveFont(num(2) == 0 ? 0 : 2));
            g2d.drawString(String.valueOf(strs[i]), x, y);
        }

        g2d.dispose();
        return image;
    }

    private float getAlpha(int i, int j) {
        int num = i + j;
        float r = 1.0F / (float)(this.len - 1);
        float s = (float)this.len * r;
        return num >= this.len ? (float)num * r - s : (float)num * r;
    }
}
