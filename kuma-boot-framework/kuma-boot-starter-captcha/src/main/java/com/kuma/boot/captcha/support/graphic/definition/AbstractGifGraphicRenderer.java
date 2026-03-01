/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.codec.Base64
 *  com.madgag.gif.fmsware.AnimatedGifEncoder
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.constant.SymbolConstants
 *  com.kuma.boot.common.utils.lang.StringUtils
 */
package com.kuma.boot.captcha.support.graphic.definition;

import cn.hutool.core.codec.Base64;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.stream.IntStream;

public abstract class AbstractGifGraphicRenderer
extends AbstractBaseGraphicRenderer {
    public AbstractGifGraphicRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public AbstractGifGraphicRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    @Override
    protected String getBase64ImagePrefix() {
        return "data:image/gif;base64,";
    }

    @Override
    public Metadata draw() {
        Object[] drawCharacters = this.getDrawCharacters();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        gifEncoder.start((OutputStream)out);
        gifEncoder.setQuality(180);
        int delay = 100;
        gifEncoder.setDelay(delay);
        gifEncoder.setRepeat(0);
        IntStream.range(0, drawCharacters.length).forEach(arg_0 -> this.lambda$draw$0((String[])drawCharacters, gifEncoder, arg_0));
        gifEncoder.finish();
        String characters = StringUtils.join((Object[])drawCharacters, (String)SymbolConstants.BLANK);
        Metadata metadata = new Metadata();
        metadata.setGraphicImageBase64(this.getBase64ImagePrefix() + Base64.encode((byte[])out.toByteArray()));
        metadata.setCharacters(characters);
        return metadata;
    }

    private /* synthetic */ void lambda$draw$0(String[] drawCharacters, AnimatedGifEncoder gifEncoder, int i) {
        BufferedImage frame = this.createGifBufferedImage(drawCharacters, i);
        gifEncoder.addFrame(frame);
        frame.flush();
    }
}

