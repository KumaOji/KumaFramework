package com.kuma.boot.encrypt.handler;

import com.kuma.boot.encrypt.exception.EncryptException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface SignEncryptHandler {
   Object handle(Object proceed, long timeout, TimeUnit timeUnit, String signSecret, Map jsonMap) throws EncryptException;
}
