package com.kuma.boot.encrypt.sign.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;
import com.kuma.boot.encrypt.sign.enums.SHAEncryptType;
import com.kuma.boot.encrypt.sign.exception.DecryptMethodNotFoundException;

public class ShaEncryptUtil {
   public static String encrypt(String str, SHAEncryptType type) {
      Digester var10000;
      switch (type) {
         case SHA1 -> var10000 = SecureUtil.sha1();
         case SHA256 -> var10000 = SecureUtil.sha256();
         default -> throw new DecryptMethodNotFoundException();
      }

      Digester digester = var10000;
      return String.valueOf(digester.digestHex(str));
   }
}
