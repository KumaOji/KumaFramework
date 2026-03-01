/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.constant.SymbolConstants
 *  com.kuma.boot.common.utils.lang.StringUtils
 */
package com.kuma.boot.captcha.support.graphic.definition;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.awt.image.BufferedImage;
import java.time.Duration;

public abstract class AbstractPngGraphicRenderer
extends AbstractBaseGraphicRenderer {
    public AbstractPngGraphicRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public AbstractPngGraphicRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    @Override
    public Metadata draw() {
        Object[] drawCharacters = this.getDrawCharacters();
        BufferedImage bufferedImage = this.createPngBufferedImage((String[])drawCharacters);
        String characters = StringUtils.join((Object[])drawCharacters, (String)SymbolConstants.BLANK);
        Metadata metadata = new Metadata();
        metadata.setGraphicImageBase64(this.toBase64(bufferedImage));
        metadata.setCharacters(characters);
        return metadata;
    }
}

