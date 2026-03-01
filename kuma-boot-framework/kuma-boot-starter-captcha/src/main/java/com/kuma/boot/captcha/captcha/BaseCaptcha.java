/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.OutputStream;

public abstract class BaseCaptcha
extends Randoms {
    protected Font font = new Font("Arial", 1, 32);
    protected int len = 4;
    protected int width = 130;
    protected int height = 48;
    protected String chars = null;
    protected int charType = 1;
    public static final int TYPE_DEFAULT = 1;
    public static final int TYPE_ONLY_NUMBER = 2;
    public static final int TYPE_ONLY_CHAR = 3;
    public static final int TYPE_ONLY_UPPER = 4;
    public static final int TYPE_ONLY_LOWER = 5;
    public static final int TYPE_NUM_AND_UPPER = 6;
    public static final int[][] COLOR = new int[][]{{0, 135, 255}, {51, 153, 51}, {255, 102, 102}, {255, 153, 0}, {153, 102, 0}, {153, 102, 153}, {51, 153, 153}, {102, 102, 255}, {0, 102, 204}, {204, 51, 51}, {0, 153, 204}, {0, 51, 102}};

    protected char[] alphas() {
        char[] cs = new char[this.len];
        block7: for (int i = 0; i < this.len; ++i) {
            switch (this.charType) {
                case 2: {
                    cs[i] = BaseCaptcha.alpha(8);
                    continue block7;
                }
                case 3: {
                    cs[i] = BaseCaptcha.alpha(8, charMaxIndex);
                    continue block7;
                }
                case 4: {
                    cs[i] = BaseCaptcha.alpha(8, 31);
                    continue block7;
                }
                case 5: {
                    cs[i] = BaseCaptcha.alpha(31, lowerMaxIndex);
                    continue block7;
                }
                case 6: {
                    cs[i] = BaseCaptcha.alpha(31);
                    continue block7;
                }
                default: {
                    cs[i] = BaseCaptcha.alpha();
                }
            }
        }
        this.chars = new String(cs);
        return cs;
    }

    protected Color color(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + BaseCaptcha.num(bc - fc);
        int g = fc + BaseCaptcha.num(bc - fc);
        int b = fc + BaseCaptcha.num(bc - fc);
        return new Color(r, g, b);
    }

    protected Color color() {
        int[] color = COLOR[BaseCaptcha.num(COLOR.length)];
        return new Color(color[0], color[1], color[2]);
    }

    public abstract boolean out(OutputStream var1);

    public String text() {
        this.checkAlpha();
        return this.chars;
    }

    public char[] textChar() {
        this.checkAlpha();
        return this.chars.toCharArray();
    }

    public void checkAlpha() {
        if (this.chars == null) {
            this.alphas();
        }
    }

    public void drawLine(int num, Graphics2D g) {
        this.drawLine(num, null, g);
    }

    public void drawLine(int num, Color color, Graphics2D g) {
        for (int i = 0; i < num; ++i) {
            g.setColor(color == null ? this.color(150, 250) : color);
            int x1 = BaseCaptcha.num(-10, this.width - 10);
            int y1 = BaseCaptcha.num(5, this.height - 5);
            int x2 = BaseCaptcha.num(10, this.width + 10);
            int y2 = BaseCaptcha.num(2, this.height - 2);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    public void drawOval(int num, Graphics2D g) {
        for (int i = 0; i < num; ++i) {
            g.setColor(this.color(100, 250));
            g.drawOval(BaseCaptcha.num(this.width), BaseCaptcha.num(this.height), 10 + BaseCaptcha.num(20), 10 + BaseCaptcha.num(20));
        }
    }

    public void drawOval(int num, Color color, Graphics2D g) {
        for (int i = 0; i < num; ++i) {
            g.setColor(color == null ? this.color(100, 250) : color);
            g.drawOval(BaseCaptcha.num(this.width), BaseCaptcha.num(this.height), 10 + BaseCaptcha.num(20), 10 + BaseCaptcha.num(20));
        }
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public int getLen() {
        return this.len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCharType() {
        return this.charType;
    }

    public void setCharType(int charType) {
        this.charType = charType;
    }
}

