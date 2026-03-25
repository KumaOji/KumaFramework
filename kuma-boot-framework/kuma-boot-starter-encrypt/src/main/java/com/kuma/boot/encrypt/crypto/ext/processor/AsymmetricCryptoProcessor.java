package com.kuma.boot.encrypt.crypto.ext.processor;

public interface AsymmetricCryptoProcessor {
   SecretKey createSecretKey();

   String decrypt(String content, String privateKey);

   String encrypt(String content, String publicKey);
}
