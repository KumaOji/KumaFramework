package com.kuma.boot.encrypt.sign.bean;

import com.kuma.boot.encrypt.sign.enums.RSAKeyType;
import java.io.Serializable;

public interface ISecurityInfo extends Serializable {
   String getKey();

   RSAKeyType getRsaKeyType();
}
