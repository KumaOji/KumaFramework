//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.definition;

import cn.hutool.core.util.IdUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.dto.Captcha;
import com.kuma.boot.captcha.support.core.dto.GraphicCaptcha;
import com.kuma.boot.captcha.support.core.dto.Verification;
import com.kuma.boot.captcha.support.core.exception.CaptchaHasExpiredException;
import com.kuma.boot.captcha.support.core.exception.CaptchaIsEmptyException;
import com.kuma.boot.captcha.support.core.exception.CaptchaMismatchException;
import com.kuma.boot.captcha.support.core.exception.CaptchaParameterIllegalException;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.awt.Font;
import java.time.Duration;
import org.apache.commons.lang3.ObjectUtils;

public abstract class AbstractGraphicRenderer extends AbstractRenderer {
    private GraphicCaptcha graphicCaptcha;

    public AbstractGraphicRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public AbstractGraphicRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    protected Font getFont() {
        return this.getResourceProvider().getGraphicFont();
    }

    protected int getWidth() {
        return this.getCaptchaProperties().getGraphics().getWidth();
    }

    protected int getHeight() {
        return this.getCaptchaProperties().getGraphics().getHeight();
    }

    protected int getLength() {
        return this.getCaptchaProperties().getGraphics().getLength();
    }

    public Captcha getCapcha(String key) {
        String identity = key;
        if (StringUtils.isBlank(key)) {
            identity = IdUtil.fastUUID();
        }

        this.create(identity);
        return this.getGraphicCaptcha();
    }

    public boolean verify(Verification verification) {
        if (!ObjectUtils.isEmpty(verification) && !StringUtils.isEmpty(verification.getIdentity())) {
            if (StringUtils.isEmpty(verification.getCharacters())) {
                throw new CaptchaIsEmptyException("Captcha is empty");
            } else {
                String store = (String)this.get(verification.getIdentity());
                if (StringUtils.isEmpty(store)) {
                    throw new CaptchaHasExpiredException("Stamp is invalid!");
                } else {
                    this.delete(verification.getIdentity());
                    String real = verification.getCharacters();
                    if (!StringUtils.equalsIgnoreCase(store, real)) {
                        throw new CaptchaMismatchException();
                    } else {
                        return true;
                    }
                }
            }
        } else {
            throw new CaptchaParameterIllegalException("Parameter value is illegal");
        }
    }

    private GraphicCaptcha getGraphicCaptcha() {
        return this.graphicCaptcha;
    }

    protected void setGraphicCaptcha(GraphicCaptcha graphicCaptcha) {
        this.graphicCaptcha = graphicCaptcha;
    }

    public String nextStamp(String key) {
        Metadata metadata = this.draw();
        GraphicCaptcha graphicCaptcha = new GraphicCaptcha();
        graphicCaptcha.setIdentity(key);
        graphicCaptcha.setGraphicImageBase64(metadata.getGraphicImageBase64());
        graphicCaptcha.setCategory(this.getCategory());
        this.setGraphicCaptcha(graphicCaptcha);
        return metadata.getCharacters();
    }
}
