package com.kuma.boot.encrypt.sign.annotation.encrypt;

import com.kuma.boot.encrypt.sign.enums.EncryptBodyMethod;
import com.kuma.boot.encrypt.sign.enums.SHAEncryptType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptBody {
   EncryptBodyMethod value() default EncryptBodyMethod.MD5;

   String otherKey() default "";

   SHAEncryptType shaType() default SHAEncryptType.SHA256;
}
