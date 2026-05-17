//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.api;

public interface SecretContext {
    SecretContext source(byte[] var1);

    byte[] source();

    String sourceText();

    SecretContext key(byte[] var1);

    byte[] key();

    String keyText();

    SecretContext charset(String var1);

    String charset();
}
