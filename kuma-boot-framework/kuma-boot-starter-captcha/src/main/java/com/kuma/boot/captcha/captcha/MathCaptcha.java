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
import javax.imageio.ImageIO;

public class MathCaptcha extends AbstractMathCaptcha {
    public MathCaptcha(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    public MathCaptcha(int width, int height, int len) {
        this(width, height);
        this.setLen(len);
    }

    public MathCaptcha(int width, int height, int len, Font font) {
        this(width, height, len);
        this.setFont(font);
    }

    public boolean out(OutputStream out) {
        char[] chars = this.alphas();
        return this.graphicsImage(chars, out);
    }

    private boolean graphicsImage(char[] strs, OutputStream out) {
        boolean ok;
        try {
            BufferedImage bi = new BufferedImage(this.width, this.height, 1);
            Graphics2D g = (Graphics2D)bi.getGraphics();
            int len = strs.length;
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, this.width, this.height);
            g.setColor(this.color());
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int hp = this.height - this.font.getSize() >> 1;
            int h = this.height - hp;
            int w = this.width / strs.length;
            int sp = (w - this.font.getSize()) / 2;

            for(int i = 0; i < len; ++i) {
                int x = i * w + sp + num(-5, 5);
                int y = h + num(-5, 5);
                if (x < 5) {
                    x = 5;
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

                g.setFont(this.font.deriveFont(num(2) == 0 ? 0 : 2));
                g.drawString(String.valueOf(strs[i]), x, y);
            }

            g.setStroke(new BasicStroke(1.25F, 0, 2));
            AlphaComposite ac3 = AlphaComposite.getInstance(3, 0.7F);
            g.setComposite(ac3);
            this.drawLine(2, g.getColor(), g);
            this.drawOval(5, g.getColor(), g);
            ImageIO.write(bi, "png", out);
            out.flush();
            ok = true;
        } catch (IOException e) {
            ok = false;
            LogUtils.error(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                LogUtils.error(e);
            }

        }

        return ok;
    }
}
