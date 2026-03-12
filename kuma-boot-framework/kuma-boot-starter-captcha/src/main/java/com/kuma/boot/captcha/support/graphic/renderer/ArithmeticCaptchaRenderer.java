//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.graphic.renderer;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCategory;
import com.kuma.boot.captcha.support.core.provider.RandomProvider;
import com.kuma.boot.captcha.support.graphic.definition.AbstractBaseGraphicRenderer;
import java.awt.image.BufferedImage;
import java.time.Duration;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class ArithmeticCaptchaRenderer extends AbstractBaseGraphicRenderer implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(ArithmeticCaptchaRenderer.class);
    private int complexity = 2;
    private String computedResult;

    public ArithmeticCaptchaRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public ArithmeticCaptchaRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    public String getCategory() {
        return CaptchaCategory.ARITHMETIC.getConstant();
    }

    protected String getBase64ImagePrefix() {
        return "data:image/png;base64,";
    }

    protected String[] getDrawCharacters() {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < this.complexity; ++i) {
            builder.append(RandomProvider.randomInt(10));
            if (i < this.complexity - 1) {
                int type = RandomProvider.randomInt(1, 4);
                if (type == 1) {
                    builder.append("+");
                } else if (type == 2) {
                    builder.append("-");
                } else if (type == 3) {
                    builder.append("x");
                }
            }
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");

        try {
            this.computedResult = String.valueOf(engine.eval(builder.toString().replaceAll("x", "*")));
        } catch (ScriptException e) {
            log.error("Arithmetic png captcha eval expression error！", e);
        }

        builder.append("=?");
        String result = builder.toString();
        return result.split("(?!^)");
    }

    public Metadata draw() {
        String[] drawContent = this.getDrawCharacters();
        BufferedImage bufferedImage = this.createArithmeticBufferedImage(drawContent);
        Metadata metadata = new Metadata();
        metadata.setGraphicImageBase64(this.toBase64(bufferedImage));
        metadata.setCharacters(this.computedResult);
        return metadata;
    }

    public void afterPropertiesSet() throws Exception {
        this.complexity = this.getCaptchaProperties().getGraphics().getComplexity();
    }
}
