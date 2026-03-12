//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.captcha.model.Captcha;
import com.kuma.boot.captcha.model.CaptchaCodeEnum;
import com.kuma.boot.captcha.model.CaptchaException;
import com.kuma.boot.captcha.model.CaptchaTypeEnum;
import com.kuma.boot.captcha.model.Point;
import com.kuma.boot.captcha.util.ImageUtils;
import com.kuma.boot.common.utils.common.RandomUtils;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.secure.AESUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ClickWordCaptchaServiceImpl extends AbstractCaptchaService {
    public static String HAN_ZI = "的一了是我不在人们有来他这上着个地到大里说就去子得也和那要下看天时过出小么起你都把好还多没为又可家学只以主会样年想生同老中十从自面前头道它后然走很像见两用她国动进成回什边作对开而己些现山民候经发工向事命给长水几义三声于高手知理眼志点心战二问但身方实吃做叫当住听革打呢真全才四已所敌之最光产情路分总条白话东席次亲如被花口放儿常气五第使写军吧文运再果怎定许快明行因别飞外树物活部门无往船望新带队先力完却站代员机更九您每风级跟笑啊孩万少直意夜比阶连车重便斗马哪化太指变社似士者干石满日决百原拿群究各六本思解立河村八难早论吗根共让相研今其书坐接应关信觉步反处记将千找争领或师结块跑谁草越字加脚紧爱等习阵怕月青半火法题建赶位唱海七女任件感准张团屋离色脸片科倒睛利世刚且由送切星导晚表够整认响雪流未场该并底深刻平伟忙提确近亮轻讲农古黑告界拉名呀土清阳照办史改历转画造嘴此治北必服雨穿内识验传业菜爬睡兴形量咱观苦体众通冲合破友度术饭公旁房极南枪读沙岁线野坚空收算至政城劳落钱特围弟胜教热展包歌类渐强数乡呼性音答哥际旧神座章帮啦受系令跳非何牛取入岸敢掉忽种装顶急林停息句区衣般报叶压慢叔背细";
    protected static String clickWordFontStr = "NotoSerif-Light.ttf";
    protected Font clickWordFont;
    private int wordTotalCount = 4;
    private boolean fontColorRandom;

    public ClickWordCaptchaServiceImpl() {
        this.fontColorRandom = Boolean.TRUE;
    }

    public String captchaType() {
        return CaptchaTypeEnum.CLICKWORD.getCodeValue();
    }

    public void init(Properties config) {
        super.init(config);
        clickWordFontStr = config.getProperty("captcha.font.type", "SourceHanSansCN-Normal.otf");

        try {
            if (!clickWordFontStr.toLowerCase().endsWith(".ttf") && !clickWordFontStr.toLowerCase().endsWith(".kmc") && !clickWordFontStr.toLowerCase().endsWith(".otf")) {
                this.clickWordFont = new Font(clickWordFontStr, 1, HAN_ZI_SIZE);
            } else {
                this.clickWordFont = Font.createFont(0, this.getClass().getResourceAsStream("/fonts/" + clickWordFontStr)).deriveFont(1, (float)HAN_ZI_SIZE);
            }
        } catch (Exception ex) {
            LogUtils.error("load font error:{}", new Object[]{ex});
        }

    }

    public void destroy(Properties config) {
        LogUtils.info("start-clear-history-data-{}", new Object[]{this.captchaType()});
    }

    public Captcha get(Captcha captcha) {
        super.get(captcha);
        BufferedImage bufferedImage = ImageUtils.getPicClick();
        if (null == bufferedImage) {
            LogUtils.error("滑动底图未初始化成功，请检查路径", new Object[0]);
            throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_BASEMAP_NULL);
        } else {
            Captcha imageData = this.getImageData(bufferedImage);
            if (StrUtil.isBlank(imageData.getOriginalImageBase64())) {
                throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_ERROR);
            } else {
                return imageData;
            }
        }
    }

    public Captcha check(Captcha captcha) {
        this.check(captcha);
        String codeKey = String.format(REDIS_CAPTCHA_KEY, captcha.getToken());
        if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
            throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_INVALID);
        } else {
            String s = CaptchaServiceFactory.getCache(cacheType).get(codeKey);
            CaptchaServiceFactory.getCache(cacheType).delete(codeKey);

            List<Point> point;
            List<Point> point1;
            String pointJson;
            try {
                point = JacksonUtils.toList(s, Point.class);
                pointJson = decrypt(captcha.getPointJson(), ((Point)point.get(0)).getSecretKey());
                point1 = JacksonUtils.toList(pointJson, Point.class);
            } catch (Exception e) {
                LogUtils.error("验证码坐标解析失败", new Object[]{e});
                this.afterValidateFail(captcha);
                throw new CaptchaException(e.getMessage());
            }

            for(int i = 0; i < point.size(); ++i) {
                if (((Point)point.get(i)).x - HAN_ZI_SIZE > ((Point)point1.get(i)).x || ((Point)point1.get(i)).x > ((Point)point.get(i)).x + HAN_ZI_SIZE || ((Point)point.get(i)).y - HAN_ZI_SIZE > ((Point)point1.get(i)).y || ((Point)point1.get(i)).y > ((Point)point.get(i)).y + HAN_ZI_SIZE) {
                    this.afterValidateFail(captcha);
                    throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_COORDINATE_ERROR);
                }
            }

            String secretKey = ((Point)point.get(0)).getSecretKey();

            String value;
            try {
                value = Base64.getEncoder().encodeToString(AESUtils.encrypt(captcha.getToken().concat("---").concat(pointJson), secretKey));
            } catch (Exception e) {
                LogUtils.error("AES加密失败", new Object[]{e});
                this.afterValidateFail(captcha);
                throw new CaptchaException(e.getMessage());
            }

            String secondKey = String.format(REDIS_SECOND_CAPTCHA_KEY, value);
            CaptchaServiceFactory.getCache(cacheType).set(secondKey, captcha.getToken(), EXPIRESIN_THREE);
            captcha.setResult(true);
            captcha.resetClientFlag();
            return captcha;
        }
    }

    public Captcha verification(Captcha captcha) {
        super.verification(captcha);

        try {
            String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY, captcha.getCaptchaVerification());
            if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
                throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_INVALID);
            } else {
                CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
                return captcha;
            }
        } catch (Exception e) {
            LogUtils.error("验证码坐标解析失败", new Object[]{e});
            throw new CaptchaException(e.getMessage());
        }
    }

    public int getWordTotalCount() {
        return this.wordTotalCount;
    }

    public void setWordTotalCount(int wordTotalCount) {
        this.wordTotalCount = wordTotalCount;
    }

    public boolean isFontColorRandom() {
        return this.fontColorRandom;
    }

    public void setFontColorRandom(boolean fontColorRandom) {
        this.fontColorRandom = fontColorRandom;
    }

    private Captcha getImageData(BufferedImage backgroundImage) {
        Captcha dataVO = new Captcha();
        List<String> wordList = new ArrayList();
        List<Point> pointList = new ArrayList();
        Graphics backgroundGraphics = backgroundImage.getGraphics();
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();
        int wordCount = this.getWordTotalCount();
        int num = RandomUtils.randomInt(1, wordCount);
        Set<String> currentWords = this.getRandomWords(wordCount);
        String secretKey = null;
        if (captchaAesStatus) {
            secretKey = RandomUtils.randomString(16);
        }

        int i = 0;

        for(String word : currentWords) {
            Point point = randomWordPoint(width, height, i, wordCount);
            point.setSecretKey(secretKey);
            if (this.isFontColorRandom()) {
                backgroundGraphics.setColor(new Color(RandomUtils.randomInt(1, 255), RandomUtils.randomInt(1, 255), RandomUtils.randomInt(1, 255)));
            } else {
                backgroundGraphics.setColor(Color.BLACK);
            }

            AffineTransform affineTransform = new AffineTransform();
            affineTransform.rotate(Math.toRadians((double)RandomUtils.randomInt(-45, 45)), (double)0.0F, (double)0.0F);
            Font rotatedFont = this.clickWordFont.deriveFont(affineTransform);
            backgroundGraphics.setFont(rotatedFont);
            backgroundGraphics.drawString(word, point.getX(), point.getY());
            if (num - 1 != i) {
                wordList.add(word);
                pointList.add(point);
            }

            ++i;
        }

        backgroundGraphics.setFont(this.waterMarkFont);
        backgroundGraphics.setColor(Color.white);
        backgroundGraphics.drawString(waterMark, width - getEnOrChLength(waterMark), height - HAN_ZI_SIZE / 2 + 7);
        BufferedImage combinedImage = new BufferedImage(width, height, 1);
        Graphics combinedGraphics = combinedImage.getGraphics();
        combinedGraphics.drawImage(backgroundImage, 0, 0, (ImageObserver)null);
        dataVO.setOriginalImageBase64(ImageUtils.getImageToBase64Str(backgroundImage).replaceAll("\r|\n", ""));
        dataVO.setWordList(wordList);
        dataVO.setToken(IdUtil.randomUUID());
        dataVO.setSecretKey(secretKey);
        String codeKey = String.format(REDIS_CAPTCHA_KEY, dataVO.getToken());
        CaptchaServiceFactory.getCache(cacheType).set(codeKey, JacksonUtils.toJSONString(pointList), EXPIRESIN_SECONDS);
        return dataVO;
    }

    private Set<String> getRandomWords(int wordCount) {
        Set<String> words = new HashSet();
        int size = HAN_ZI.length();

        do {
            String var10000 = HAN_ZI;
            String t = "" + var10000.charAt(RandomUtils.randomInt(size));
            words.add(t);
        } while(words.size() < wordCount);

        return words;
    }

    private static Point randomWordPoint(int imageWidth, int imageHeight, int wordSortIndex, int wordCount) {
        int avgWidth = imageWidth / (wordCount + 1);
        int x;
        if (avgWidth < HAN_ZI_SIZE_HALF) {
            x = RandomUtils.randomInt(1 + HAN_ZI_SIZE_HALF, imageWidth);
        } else if (wordSortIndex == 0) {
            x = RandomUtils.randomInt(1 + HAN_ZI_SIZE_HALF, avgWidth * (wordSortIndex + 1) - HAN_ZI_SIZE_HALF);
        } else {
            x = RandomUtils.randomInt(avgWidth * wordSortIndex + HAN_ZI_SIZE_HALF, avgWidth * (wordSortIndex + 1) - HAN_ZI_SIZE_HALF);
        }

        int y = RandomUtils.randomInt(HAN_ZI_SIZE, imageHeight - HAN_ZI_SIZE);
        return new Point(x, y, (String)null);
    }
}
