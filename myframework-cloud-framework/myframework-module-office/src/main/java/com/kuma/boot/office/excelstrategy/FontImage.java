/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lang.StringUtils
 */
package com.kuma.boot.office.excelstrategy;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class FontImage {
    public static BufferedImage createWatermarkImage(Watermark watermark) {
        if (watermark == null) {
            watermark = new Watermark();
            watermark.setEnable(true);
            watermark.setText("\u5185\u90e8\u8d44\u6599");
            watermark.setColor("#C5CBCF");
            watermark.setDateFormat("yyyy-MM-dd HH:mm");
        } else {
            if (StringUtils.isEmpty((String)watermark.getDateFormat())) {
                watermark.setDateFormat("yyyy-MM-dd HH:mm");
            } else if (watermark.getDateFormat().length() == 16) {
                watermark.setDateFormat("yyyy-MM-dd HH:mm");
            } else if (watermark.getDateFormat().length() == 10) {
                watermark.setDateFormat("yyyy-MM-dd");
            }
            if (StringUtils.isEmpty((String)watermark.getText())) {
                watermark.setText("\u5185\u90e8\u8d44\u6599");
            }
            if (StringUtils.isEmpty((String)watermark.getColor())) {
                watermark.setColor("#C5CBCF");
            }
        }
        String[] textArray = watermark.getText().split("\n");
        Font font = new Font("microsoft-yahei", 0, 20);
        Integer width = 300;
        Integer height = 100;
        BufferedImage image = new BufferedImage(width, height, 1);
        Graphics2D g = image.createGraphics();
        image = g.getDeviceConfiguration().createCompatibleImage(width, height, 3);
        g.dispose();
        g = image.createGraphics();
        g.setColor(new Color(Integer.parseInt(watermark.getColor().substring(1), 16)));
        g.setFont(font);
        g.shear(0.1, -0.26);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int y = 50;
        for (int i = 0; i < textArray.length; ++i) {
            g.drawString(textArray[i], 0, y);
            y += font.getSize();
        }
        g.dispose();
        return image;
    }

    public static class Watermark {
        private Boolean enable;
        private String text;
        private String dateFormat;
        private String color;

        public Boolean getEnable() {
            return this.enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDateFormat() {
            return this.dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        public String getColor() {
            return this.color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}

