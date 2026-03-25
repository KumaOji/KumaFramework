package com.kuma.boot.encrypt.handler;

public interface EncryptHandler {
   byte[] encode(byte[] content);

   byte[] decode(byte[] content);
}
