//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.graphic.definition;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.AbstractGraphicRenderer;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCharacter;
import com.kuma.boot.captcha.support.core.provider.RandomProvider;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.List;

public abstract class AbstractBaseGraphicRenderer extends AbstractGraphicRenderer {
    public AbstractBaseGraphicRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public AbstractBaseGraphicRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    protected String[] getWordCharacters() {
        int number = this.getCaptchaProperties().getGraphics().getLength();
        List<String> words = RandomProvider.randomWords(number);
        String[] content = new String[words.size()];
        return (String[])words.toArray(content);
    }

    protected String[] getCharCharacters() {
        int number = this.getCaptchaProperties().getGraphics().getLength();
        CaptchaCharacter captchaCharacter = this.getCaptchaProperties().getGraphics().getLetter();
        return RandomProvider.randomCharacters(number, captchaCharacter);
    }

    protected abstract String[] getDrawCharacters();

    private BufferedImage createPngBufferedImage(String[] characters, String benchmark, boolean isArithmetic) {
        return this.createBufferedImage(characters, benchmark, isArithmetic, false, 0);
    }

    protected BufferedImage createPngBufferedImage(String[] characters) {
        return this.createPngBufferedImage(characters, "W", false);
    }

    protected BufferedImage createArithmeticBufferedImage(String[] characters) {
        return this.createPngBufferedImage(characters, "8", true);
    }

    protected BufferedImage createGifBufferedImage(String[] characters, int alpha) {
        return this.createBufferedImage(characters, "王", false, true, alpha);
    }

    private BufferedImage createBufferedImage(String[] characters, String benchmark, boolean isArithmetic, boolean isGif, int alpha) {
        BufferedImage bufferedImage = new BufferedImage(this.getWidth(), this.getHeight(), 1);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (!isArithmetic) {
            this.drawInterfereLine(graphics, isGif);
        }

        Color[] colors = RandomProvider.randomColors(characters.length);
        this.drawCharacter(graphics, characters, colors, benchmark, isGif, alpha);
        graphics.dispose();
        return bufferedImage;
    }

    private void drawColor(Graphics2D graphics) {
        graphics.setColor(RandomProvider.randomColor());
    }

    private void drawAlpha(Graphics2D graphics, float alpha) {
        AlphaComposite alphaComposite = AlphaComposite.getInstance(3, alpha);
        graphics.setComposite(alphaComposite);
    }

    private void drawAlphaForLine(Graphics2D graphics) {
        this.drawAlpha(graphics, 0.7F);
    }

    private float getAlpha(int length, int alpha, int index) {
        int num = alpha + index;
        float r = 1.0F / (float)length;
        float s = (float)(length + 1) * r;
        return num > length ? (float)num * r - s : (float)num * r;
    }

    private void drawAlphaForCharacter(Graphics2D graphics, int length, int alpha, int index) {
        this.drawAlpha(graphics, this.getAlpha(length, alpha, index));
    }

    private int randomCtrlX() {
        return RandomProvider.randomInt(this.getWidth() / 4, this.getWidth() / 4 * 3);
    }

    private int randomCtrlY() {
        return RandomProvider.randomInt(5, this.getHeight() - 5);
    }

    private void drawBezierCurve(Graphics2D graphics) {
        this.drawColor(graphics);
        int x1 = 5;
        int y1 = RandomProvider.randomInt(5, this.getHeight() / 2);
        int x2 = this.getWidth() - 5;
        int y2 = RandomProvider.randomInt(this.getHeight() / 2, this.getHeight() - 5);
        int ctrlx1 = this.randomCtrlX();
        int ctrly1 = this.randomCtrlY();
        if (RandomProvider.randomInt(2) == 0) {
            int ty = y1;
            y1 = y2;
            y2 = ty;
        }

        if (RandomProvider.randomInt(2) == 0) {
            QuadCurve2D shape = new QuadCurve2D.Double();
            shape.setCurve((double)x1, (double)y1, (double)ctrlx1, (double)ctrly1, (double)x2, (double)y2);
            graphics.draw(shape);
        } else {
            int ctrlx2 = this.randomCtrlX();
            int ctrly2 = this.randomCtrlY();
            CubicCurve2D shape = new CubicCurve2D.Double((double)x1, (double)y1, (double)ctrlx1, (double)ctrly1, (double)ctrlx2, (double)ctrly2, (double)x2, (double)y2);
            graphics.draw(shape);
        }

    }

    private void drawInterfereLine(Graphics2D graphics, boolean isGif) {
        if (isGif) {
            this.drawAlphaForLine(graphics);
        }

        graphics.setStroke(new BasicStroke(1.2F, 0, 2));
        this.drawBezierCurve(graphics);
    }

    private void drawOval(Graphics2D graphics) {
        int x = RandomProvider.randomInt(this.getWidth() - 5);
        int y = RandomProvider.randomInt(this.getHeight() - 5);
        int width = RandomProvider.randomInt(5, 30);
        int height = 5 + RandomProvider.randomInt(5, 30);
        graphics.drawOval(x, y, width, height);
    }

    private void drawCharacter(Graphics2D graphics, String[] characters, Color[] colors, String benchmark, boolean isGif, int alpha) {
        graphics.setFont(this.getFont());
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int fW = this.getWidth() / characters.length;
        int fSp = (fW - (int)fontMetrics.getStringBounds(benchmark, graphics).getWidth()) / 2;

        for(int i = 0; i < characters.length; ++i) {
            if (isGif) {
                this.drawAlphaForCharacter(graphics, characters.length, alpha, i);
            }

            graphics.setColor(colors[i]);
            this.drawOval(graphics);
            int fY = this.getHeight() - (this.getHeight() - (int)fontMetrics.getStringBounds(String.valueOf(characters[i]), graphics).getHeight() >> 1);
            graphics.drawString(characters[i], i * fW + fSp - 3, fY - 3);
        }

    }
}
