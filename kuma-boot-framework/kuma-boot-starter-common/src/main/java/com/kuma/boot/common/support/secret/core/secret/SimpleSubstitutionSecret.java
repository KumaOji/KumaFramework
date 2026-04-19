//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;

import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.utils.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * SimpleSubstitutionSecret
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class SimpleSubstitutionSecret extends AbstractSecret {

    public byte[] doEncrypt( SecretContext context ) {
        String source = context.sourceText();
        String key = context.keyText();
        Map<Character, Character> map = this.buildMap("ABCDEFGHIJKLMNOPQRSTUVWSXYZ", key);
        return StringUtils.getBytes(this.buildResult(source, map), context.charset());
    }

    public byte[] doDecrypt( SecretContext context ) {
        String source = context.sourceText();
        String key = context.keyText();
        Map<Character, Character> map = this.buildMap(key, "ABCDEFGHIJKLMNOPQRSTUVWSXYZ");
        return StringUtils.getBytes(this.buildResult(source, map), context.charset());
    }

    private String buildResult( String source, Map<Character, Character> map ) {
        if (StringUtils.isEmpty(source)) {
            return source;
        } else {
            char[] chars = source.toCharArray();
            StringBuilder stringBuilder = new StringBuilder();

            for (char c : chars) {
                Character mapChar = (Character) map.get(c);
                if (mapChar == null) {
                    mapChar = c;
                }

                stringBuilder.append(String.valueOf(mapChar));
            }

            return stringBuilder.toString();
        }
    }

    private Map<Character, Character> buildMap( String keys, String values ) {
        Map<Character, Character> map = new HashMap(26);
        char[] kyeChars = keys.toCharArray();
        char[] valueChars = values.toCharArray();

        for (int i = 0; i < 26; ++i) {
            map.put(kyeChars[i], valueChars[i]);
        }

        return map;
    }
}
