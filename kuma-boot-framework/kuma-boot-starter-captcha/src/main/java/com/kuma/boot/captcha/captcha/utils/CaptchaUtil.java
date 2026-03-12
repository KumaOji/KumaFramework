//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.captcha.utils;

import com.kuma.boot.captcha.captcha.BaseCaptcha;
import com.kuma.boot.captcha.captcha.ChineseCaptcha;
import com.kuma.boot.captcha.captcha.ChineseGifCaptcha;
import com.kuma.boot.captcha.captcha.GifCaptcha;
import com.kuma.boot.captcha.captcha.MathCaptcha;
import com.kuma.boot.captcha.captcha.MathGifCaptcha;
import com.kuma.boot.captcha.captcha.SpecCaptcha;
import java.awt.Font;
import java.io.OutputStream;
import java.util.Random;

public class CaptchaUtil {
    public static String out(OutputStream outputStream) {
        return out(5, outputStream);
    }

    public static String out(int len, OutputStream outputStream) {
        return out(130, 48, len, outputStream);
    }

    public static String out(int len, Font font, OutputStream outputStream) {
        return out(130, 48, len, font, outputStream);
    }

    public static String out(int width, int height, int len, OutputStream outputStream) {
        return out(width, height, len, (Font)null, outputStream);
    }

    public static String out(int width, int height, int len, Font font, OutputStream outputStream) {
        int cType = (new Random()).nextInt(6);
        return outCaptcha(width, height, len, font, cType, outputStream);
    }

    public static String outPng(OutputStream outputStream) {
        return outPng(5, outputStream);
    }

    public static String outPng(int len, OutputStream outputStream) {
        return outPng(130, 48, len, outputStream);
    }

    public static String outPng(int len, Font font, OutputStream outputStream) {
        return outPng(130, 48, len, font, outputStream);
    }

    public static String outPng(int width, int height, int len, OutputStream outputStream) {
        return outPng(width, height, len, (Font)null, outputStream);
    }

    public static String outPng(int width, int height, int len, Font font, OutputStream outputStream) {
        int cType = (new Random()).nextInt(6);
        return outCaptcha(width, height, len, font, cType, outputStream);
    }

    private static String outCaptcha(int width, int height, int len, Font font, int cType, OutputStream outputStream) {
        BaseCaptcha captcha = null;
        if (cType == 0) {
            captcha = new SpecCaptcha(width, height, len);
        } else if (cType == 1) {
            captcha = new GifCaptcha(width, height, len);
        } else if (cType == 2) {
            captcha = new ChineseCaptcha(width, height, len);
        } else if (cType == 3) {
            captcha = new ChineseGifCaptcha(width, height, len);
        } else if (cType == 4) {
            captcha = new MathCaptcha(width, height, 4);
        } else if (cType == 5) {
            captcha = new MathGifCaptcha(width, height, 4);
        }

        if (font != null) {
            captcha.setFont(font);
        }

        captcha.out(outputStream);
        return captcha.text().toLowerCase();
    }
}
