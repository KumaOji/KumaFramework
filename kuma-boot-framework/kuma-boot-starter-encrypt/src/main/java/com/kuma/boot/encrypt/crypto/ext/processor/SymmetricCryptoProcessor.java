package com.kuma.boot.encrypt.crypto.ext.processor;

public interface SymmetricCryptoProcessor {
   String createKey();

   String decrypt(String data, String key);

   String encrypt(String data, String key);
}
