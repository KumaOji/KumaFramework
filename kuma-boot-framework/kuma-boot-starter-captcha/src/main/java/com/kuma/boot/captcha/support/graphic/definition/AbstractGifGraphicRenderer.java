//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.graphic.definition;

import cn.hutool.core.codec.Base64;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.stream.IntStream;

public abstract class AbstractGifGraphicRenderer extends AbstractBaseGraphicRenderer {
    public AbstractGifGraphicRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public AbstractGifGraphicRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    protected String getBase64ImagePrefix() {
        return "data:image/gif;base64,";
    }

    public Metadata draw() {
        String[] drawCharacters = this.getDrawCharacters();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        gifEncoder.start(out);
        gifEncoder.setQuality(180);
        int delay = 100;
        gifEncoder.setDelay(delay);
        gifEncoder.setRepeat(0);
        IntStream.range(0, drawCharacters.length).forEach((i) -> {
            BufferedImage frame = this.createGifBufferedImage(drawCharacters, i);
            gifEncoder.addFrame(frame);
            frame.flush();
        });
        gifEncoder.finish();
        String characters = StringUtils.join(drawCharacters, SymbolConstants.BLANK);
        Metadata metadata = new Metadata();
        String var10001 = this.getBase64ImagePrefix();
        metadata.setGraphicImageBase64(var10001 + Base64.encode(out.toByteArray()));
        metadata.setCharacters(characters);
        return metadata;
    }
}
