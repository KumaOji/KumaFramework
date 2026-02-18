//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;


import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.xkzhangsan.time.utils.StringUtil;

/**
 * SingleAddSecret
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class SingleAddSecret extends AbstractSecret {

    public byte[] doEncrypt( SecretContext context ) {
        String source = context.sourceText();
        int offset = Integer.parseInt(context.keyText());
        return StringUtils.getBytes(this.shiftString(source, offset), context.charset());
    }

    public byte[] doDecrypt( SecretContext context ) {
        String source = context.sourceText();
        int offset = Integer.parseInt(context.keyText());
        return StringUtils.getBytes(this.shiftString(source, -offset), context.charset());
    }

    private String shiftString( String source, int offset ) {
        if (StringUtil.isEmpty(source)) {
            return source;
        } else {
            Character[] characters = StringUtils.toCharacterArray(source);
            Object[] newChars = ArrayUtils.shift(characters, offset);
            return StringUtils.join(newChars, "", 0, newChars.length - 1);
        }
    }
}
