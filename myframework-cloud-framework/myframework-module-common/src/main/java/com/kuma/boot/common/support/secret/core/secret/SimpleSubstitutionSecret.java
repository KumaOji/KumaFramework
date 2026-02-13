/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.xkzhangsan.time.utils.StringUtil
 */
package com.kuma.boot.common.support.secret.core.secret;

import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.secret.AbstractSecret;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.xkzhangsan.time.utils.StringUtil;
import java.util.HashMap;
import java.util.Map;

public class SimpleSubstitutionSecret
extends AbstractSecret {
    @Override
    public byte[] doEncrypt(SecretContext context) {
        String source = context.sourceText();
        String key = context.keyText();
        Map<Character, Character> map = this.buildMap("ABCDEFGHIJKLMNOPQRSTUVWSXYZ", key);
        return StringUtils.getBytes(this.buildResult(source, map), context.charset());
    }

    @Override
    public byte[] doDecrypt(SecretContext context) {
        String source = context.sourceText();
        String key = context.keyText();
        Map<Character, Character> map = this.buildMap(key, "ABCDEFGHIJKLMNOPQRSTUVWSXYZ");
        return StringUtils.getBytes(this.buildResult(source, map), context.charset());
    }

    private String buildResult(String source, Map<Character, Character> map) {
        if (StringUtil.isEmpty((String)source)) {
            return source;
        }
        char[] chars = source.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : chars) {
            Character mapChar = map.get(Character.valueOf(c));
            if (mapChar == null) {
                mapChar = Character.valueOf(c);
            }
            stringBuilder.append(String.valueOf(mapChar));
        }
        return stringBuilder.toString();
    }

    private Map<Character, Character> buildMap(String keys, String values) {
        HashMap<Character, Character> map = new HashMap<Character, Character>(26);
        char[] kyeChars = keys.toCharArray();
        char[] valueChars = values.toCharArray();
        for (int i = 0; i < 26; ++i) {
            map.put(Character.valueOf(kyeChars[i]), Character.valueOf(valueChars[i]));
        }
        return map;
    }
}

