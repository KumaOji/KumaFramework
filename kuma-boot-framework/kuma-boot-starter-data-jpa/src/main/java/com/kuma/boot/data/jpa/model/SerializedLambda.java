package com.kuma.boot.data.jpa.model;

import com.kuma.boot.common.exception.BusinessException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import org.apache.commons.lang3.SerializationUtils;

public final class SerializedLambda implements Serializable {
   private static final long serialVersionUID = 8025925345765570181L;
   private String implMethodName;

   public SerializedLambda() {
   }

   public static SerializedLambda resolve(SFunction lambda) {
      if (!lambda.getClass().isSynthetic()) {
         throw new BusinessException("\u8be5\u65b9\u6cd5\u4ec5\u80fd\u4f20\u5165 lambda \u8868\u8fbe\u5f0f\u4ea7\u751f\u7684\u5408\u6210\u7c7b");
      } else {
         try {
            ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(SerializationUtils.serialize(lambda))) {
               protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                  Class<?> clazz = super.resolveClass(objectStreamClass);
                  return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
               }
            };

            SerializedLambda var2;
            try {
               var2 = (SerializedLambda)objIn.readObject();
            } catch (Throwable var5) {
               try {
                  objIn.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }

               throw var5;
            }

            objIn.close();
            return var2;
         } catch (IOException | ClassNotFoundException var6) {
            throw new BusinessException("This is impossible to happen");
         }
      }
   }

   public String getImplMethodName() {
      return this.implMethodName;
   }
}
