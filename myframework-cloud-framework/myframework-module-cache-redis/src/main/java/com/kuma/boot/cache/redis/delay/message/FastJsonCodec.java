/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  org.redisson.client.codec.StringCodec
 *  org.redisson.client.protocol.Decoder
 *  org.redisson.client.protocol.Encoder
 *  tools.jackson.databind.JsonNode
 */
package com.kuma.boot.cache.redis.delay.message;

import com.kuma.boot.common.utils.json.JacksonUtils;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import tools.jackson.databind.JsonNode;

public class FastJsonCodec
extends StringCodec {
    public static final FastJsonCodec INSTANCE = new FastJsonCodec();
    private Encoder encoder = object -> {
        String jsonStr = JacksonUtils.toJSONString((Object)object);
        return super.getValueEncoder().encode((Object)jsonStr);
    };
    private Decoder<Object> decoder = (buf, state) -> {
        byte[] result = new byte[buf.readableBytes()];
        buf.readBytes(result);
        String message = new String(result, StandardCharsets.UTF_8);
        JsonNode jsonNode = JacksonUtils.parse((String)message);
        assert (jsonNode != null);
        String payload = jsonNode.get("payload").toString();
        Map headers = JacksonUtils.readMap((String)jsonNode.get("headers").toString());
        return new RedissonMessage(payload, headers);
    };

    public FastJsonCodec() {
        this(StandardCharsets.UTF_8);
    }

    public FastJsonCodec(Charset charset) {
        super(charset);
    }

    public Decoder<Object> getValueDecoder() {
        return this.decoder;
    }

    public Encoder getValueEncoder() {
        return this.encoder;
    }
}

