//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.api;

public interface Secret {
    byte[] encrypt(SecretContext var1);

    byte[] decrypt(SecretContext var1);
}
