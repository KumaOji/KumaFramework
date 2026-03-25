package com.kuma.boot.encrypt.encrypt2.codec;

public interface SecurityProcessor {
   byte[] encrypt(byte[] data);

   byte[] decrypt(String text);
}
