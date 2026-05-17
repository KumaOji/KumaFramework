//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;


import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.lang.StringUtils;

/**
 * CaesarShiftSecret
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class CaesarShiftSecret extends AbstractSecret {

    public byte[] doEncrypt( SecretContext context ) {
        String source = context.sourceText();
        int offset = Integer.parseInt(context.keyText());
        String text = this.shiftString(source, offset);
        return StringUtils.getBytes(text, context.charset());
    }

    public byte[] doDecrypt( SecretContext context ) {
        String source = context.sourceText();
        int offset = Integer.parseInt(context.keyText());
        String text = this.shiftString(source, -offset);
        return StringUtils.getBytes(text, context.charset());
    }

    private String shiftString( String source, int offset ) {
        if (StringUtils.isEmpty(source)) {
            return source;
        } else {
            Character[] characters = StringUtils.toCharacterArray(source);
            Object[] newChars = ArrayUtils.shift(characters, offset);
            return StringUtils.join(newChars, "", 0, newChars.length);
        }
    }
}
