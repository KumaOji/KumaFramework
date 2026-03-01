/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.cache.redis.delay.message;

// import com.alibaba.fastjson2.JSON;
// import com.alibaba.fastjson2.JSONObject;
import tools.jackson.databind.JsonNode;
import com.kuma.boot.common.utils.json.JacksonUtils;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

/**
 * FastJsonCodec
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:41
 */
public class FastJsonCodec extends StringCodec {

    public static final FastJsonCodec INSTANCE = new FastJsonCodec();

    private Encoder encoder;

    private Decoder<Object> decoder;

    public FastJsonCodec() {
        this(StandardCharsets.UTF_8);
    }

    public FastJsonCodec(Charset charset) {
        super(charset);

        this.encoder = object -> {
            String jsonStr = JacksonUtils.toJSONString(object);
            return super.getValueEncoder().encode(jsonStr);
        };

        this.decoder = (buf, state) -> {
            byte[] result = new byte[buf.readableBytes()];
            buf.readBytes(result);
            String message = new String(result, StandardCharsets.UTF_8);
            JsonNode jsonNode = JacksonUtils.parse(message);

            assert jsonNode != null;
            String payload = jsonNode.get("payload").toString();
            Map<String, Object> headers =
                    JacksonUtils.readMap(jsonNode.get("headers").toString());

            // JsonNode payload = JsonUtil.parse(new String(result,
            // StandardCharsets.UTF_8)).get("payload");
            // String payloadStr =
            // payload.traverse(JsonUtil.MAPPER).readValueAs(String.class);
            //
            // JsonNode headers = JsonUtil.parse(new String(result,
            // StandardCharsets.UTF_8)).get("headers");
            // Map<String, Object> headersMap =
            // headers.traverse(JsonUtil.MAPPER).readValueAs(Map.class);

            // JSONObject jsonObject = (JSONObject) JSON.parse(result);
            // Object payload = jsonObject.get("payload");
            // String payloadStr;
            // if (payload instanceof JSON) {
            //    payloadStr = ((JSON) payload).toJSONString();
            // } else {
            //    payloadStr = JSON.toJSONString(payload);
            // }
            // JSONObject headers = jsonObject.getJSONObject("headers");

            return new RedissonMessage(payload, headers);
        };
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return this.decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return this.encoder;
    }
}
