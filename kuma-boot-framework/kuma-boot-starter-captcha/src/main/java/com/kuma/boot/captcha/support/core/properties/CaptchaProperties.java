//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.properties;

import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCharacter;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaFont;
import com.kuma.boot.captcha.support.core.definition.enums.FontStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "kuma.boot.captcha.tmp"
)
public class CaptchaProperties {
    private boolean enabled = false;
    private Graphics graphics = new Graphics();
    private Watermark watermark = new Watermark();
    private Jigsaw jigsaw = new Jigsaw();
    private WordClick wordClick = new WordClick();

    public Graphics getGraphics() {
        return this.graphics;
    }

    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }

    public Watermark getWatermark() {
        return this.watermark;
    }

    public void setWatermark(Watermark watermark) {
        this.watermark = watermark;
    }

    public Jigsaw getJigsaw() {
        return this.jigsaw;
    }

    public void setJigsaw(Jigsaw jigsaw) {
        this.jigsaw = jigsaw;
    }

    public WordClick getWordClick() {
        return this.wordClick;
    }

    public void setWordClick(WordClick wordClick) {
        this.wordClick = wordClick;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static class Graphics {
        private int length = 5;
        private int width = 130;
        private int height = 48;
        private int complexity = 2;
        private CaptchaCharacter letter;
        private CaptchaFont font;

        public Graphics() {
            this.letter = CaptchaCharacter.NUM_AND_CHAR;
            this.font = CaptchaFont.LEXOGRAPHER;
        }

        public int getLength() {
            return this.length;
        }

        public void setLength(int length) {
            this.length = length;
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

        public CaptchaFont getFont() {
            return this.font;
        }

        public void setFont(CaptchaFont captchaFont) {
            this.font = captchaFont;
        }

        public CaptchaCharacter getLetter() {
            return this.letter;
        }

        public void setLetter(CaptchaCharacter letter) {
            this.letter = letter;
        }

        public int getComplexity() {
            return this.complexity;
        }

        public void setComplexity(int complexity) {
            this.complexity = complexity;
        }
    }

    public static class Watermark {
        private String content = "Dante Cloud";
        private String fontName = "WenQuanZhengHei.ttf";
        private FontStyle fontStyle;
        private Integer fontSize;

        public Watermark() {
            this.fontStyle = FontStyle.BOLD;
            this.fontSize = 25;
        }

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getFontName() {
            return this.fontName;
        }

        public void setFontName(String fontName) {
            this.fontName = fontName;
        }

        public Integer getFontSize() {
            return this.fontSize;
        }

        public void setFontSize(Integer fontSize) {
            this.fontSize = fontSize;
        }

        public FontStyle getFontStyle() {
            return this.fontStyle;
        }

        public void setFontStyle(FontStyle fontStyle) {
            this.fontStyle = fontStyle;
        }
    }

    public static class Jigsaw {
        private String originalResource = "classpath*:images/jigsaw/original/*.png";
        private String templateResource = "classpath*:images/jigsaw/template/*.png";
        private Integer interference = 0;
        private Integer deviation = 5;

        public String getOriginalResource() {
            return this.originalResource;
        }

        public void setOriginalResource(String originalResource) {
            this.originalResource = originalResource;
        }

        public String getTemplateResource() {
            return this.templateResource;
        }

        public void setTemplateResource(String templateResource) {
            this.templateResource = templateResource;
        }

        public Integer getInterference() {
            return this.interference;
        }

        public void setInterference(Integer interference) {
            this.interference = interference;
        }

        public Integer getDeviation() {
            return this.deviation;
        }

        public void setDeviation(Integer deviation) {
            this.deviation = deviation;
        }
    }

    public static class WordClick {
        private String imageResource = "classpath*:images/word-click/*.png";
        private Integer wordCount = 5;
        private boolean randomColor = true;
        private FontStyle fontStyle;
        private String fontName;
        private Integer fontSize;

        public WordClick() {
            this.fontStyle = FontStyle.BOLD;
            this.fontName = "WenQuanZhengHei.ttf";
            this.fontSize = 25;
        }

        public String getImageResource() {
            return this.imageResource;
        }

        public void setImageResource(String imageResource) {
            this.imageResource = imageResource;
        }

        public Integer getWordCount() {
            return this.wordCount;
        }

        public void setWordCount(Integer wordCount) {
            this.wordCount = wordCount;
        }

        public Integer getFontSize() {
            return this.fontSize;
        }

        public void setFontSize(Integer fontSize) {
            this.fontSize = fontSize;
        }

        public boolean isRandomColor() {
            return this.randomColor;
        }

        public void setRandomColor(boolean randomColor) {
            this.randomColor = randomColor;
        }

        public String getFontName() {
            return this.fontName;
        }

        public void setFontName(String fontName) {
            this.fontName = fontName;
        }

        public FontStyle getFontStyle() {
            return this.fontStyle;
        }

        public void setFontStyle(FontStyle fontStyle) {
            this.fontStyle = fontStyle;
        }
    }
}
