/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.wf.captcha.ArithmeticCaptcha
 */
package com.kuma.boot.captcha.util;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.wf.captcha.ArithmeticCaptcha;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public final class CaptchaUtils {
    private static final int width = 200;
    private static final int height = 50;
    private static Random random;

    private CaptchaUtils() {
    }

    public static ArithmeticCaptcha getArithmeticCaptcha() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(200, 50);
        captcha.setLen(2);
        return captcha;
    }

    public static BufferedImage createImage() {
        return new BufferedImage(200, 50, 1);
    }

    public static String drawRandomText(BufferedImage verifyImg) {
        int i;
        Graphics2D graphics = (Graphics2D)verifyImg.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 200, 50);
        graphics.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", 0, 30));
        String baseNumLetter = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
        StringBuilder sBuffer = new StringBuilder();
        int x = 10;
        for (i = 0; i < 4; ++i) {
            graphics.setColor(CaptchaUtils.getRandomColor());
            int degree = random.nextInt() % 30;
            int dot = random.nextInt(baseNumLetter.length());
            String ch = "" + baseNumLetter.charAt(dot);
            sBuffer.append(ch);
            graphics.rotate((double)degree * Math.PI / 180.0, x, 45.0);
            graphics.drawString(ch, x, 45);
            graphics.rotate((double)(-degree) * Math.PI / 180.0, x, 45.0);
            x += 48;
        }
        for (i = 0; i < 6; ++i) {
            graphics.setColor(CaptchaUtils.getRandomColor());
            graphics.drawLine(random.nextInt(200), random.nextInt(50), random.nextInt(200), random.nextInt(50));
        }
        for (i = 0; i < 30; ++i) {
            int x1 = random.nextInt(200);
            int y1 = random.nextInt(50);
            graphics.setColor(CaptchaUtils.getRandomColor());
            graphics.fillRect(x1, y1, 2, 1);
        }
        return sBuffer.toString();
    }

    private static Color getRandomColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public String getCacheKey(String key, String targetName, String methodName, Object[] arguments) {
        StringBuilder sb = new StringBuilder();
        if (key != null && key.length() > 0) {
            sb.append(key);
        } else {
            sb.append(targetName).append(".").append(methodName);
        }
        if (arguments != null && arguments.length != 0) {
            sb.append("#").append(JacksonUtils.toJSONString((Object)arguments));
        }
        return sb.toString().replace("[", "").replace("\"", "").replace("]", "").replace("com.gofun.", "");
    }

    static {
        try {
            random = SecureRandom.getInstanceStrong();
        }
        catch (NoSuchAlgorithmException e) {
            LogUtils.error((Throwable)e);
        }
    }
}

