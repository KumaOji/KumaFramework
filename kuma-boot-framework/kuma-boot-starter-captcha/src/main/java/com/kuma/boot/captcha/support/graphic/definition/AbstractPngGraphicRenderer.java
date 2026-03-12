//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.graphic.definition;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.awt.image.BufferedImage;
import java.time.Duration;

public abstract class AbstractPngGraphicRenderer extends AbstractBaseGraphicRenderer {
    public AbstractPngGraphicRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public AbstractPngGraphicRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    public Metadata draw() {
        String[] drawCharacters = this.getDrawCharacters();
        BufferedImage bufferedImage = this.createPngBufferedImage(drawCharacters);
        String characters = StringUtils.join(drawCharacters, SymbolConstants.BLANK);
        Metadata metadata = new Metadata();
        metadata.setGraphicImageBase64(this.toBase64(bufferedImage));
        metadata.setCharacters(characters);
        return metadata;
    }
}
