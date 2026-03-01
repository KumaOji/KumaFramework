/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.IdUtil
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  org.apache.commons.lang3.ObjectUtils
 */
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

public abstract class AbstractGraphicRenderer
extends AbstractRenderer {
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

    @Override
    public Captcha getCapcha(String key) {
        String identity = key;
        if (StringUtils.isBlank((String)identity)) {
            identity = IdUtil.fastUUID();
        }
        this.create(identity);
        return this.getGraphicCaptcha();
    }

    @Override
    public boolean verify(Verification verification) {
        if (ObjectUtils.isEmpty((Object)verification) || StringUtils.isEmpty((String)verification.getIdentity())) {
            throw new CaptchaParameterIllegalException("Parameter value is illegal");
        }
        if (StringUtils.isEmpty((String)verification.getCharacters())) {
            throw new CaptchaIsEmptyException("Captcha is empty");
        }
        String store = (String)this.get(verification.getIdentity());
        if (StringUtils.isEmpty((String)store)) {
            throw new CaptchaHasExpiredException("Stamp is invalid!");
        }
        this.delete(verification.getIdentity());
        String real = verification.getCharacters();
        if (!StringUtils.equalsIgnoreCase((CharSequence)store, (CharSequence)real)) {
            throw new CaptchaMismatchException();
        }
        return true;
    }

    private GraphicCaptcha getGraphicCaptcha() {
        return this.graphicCaptcha;
    }

    protected void setGraphicCaptcha(GraphicCaptcha graphicCaptcha) {
        this.graphicCaptcha = graphicCaptcha;
    }

    @Override
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

