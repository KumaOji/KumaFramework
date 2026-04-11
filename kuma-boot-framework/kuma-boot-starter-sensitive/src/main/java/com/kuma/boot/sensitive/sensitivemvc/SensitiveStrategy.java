package com.kuma.boot.sensitive.sensitivemvc;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sensitive.sensitivemvc.annocation.SensitiveFilterWords;
import com.kuma.boot.sensitive.sensitivemvc.annocation.SensitiveHandler;
import com.kuma.boot.sensitive.sensitivemvc.annocation.SensitiveKeepLength;
import com.kuma.boot.sensitive.sensitivemvc.util.SensitiveUtils;
import java.lang.reflect.Field;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public enum SensitiveStrategy {
   CHINESE_NAME {
      public String apply(SensitiveWrapper wrapper) {
         return SensitiveUtils.chineseName(wrapper.getFieldValue(), wrapper.getReplacer());
      }
   },
   ID_CARD {
      public String apply(SensitiveWrapper wrapper) {
         return SensitiveUtils.idCardNum(wrapper.getFieldValue(), 1, 2, wrapper.getReplacer());
      }
   },
   FIXED_PHONE {
      public String apply(SensitiveWrapper wrapper) {
         return SensitiveUtils.fixedPhone(wrapper.getFieldValue(), wrapper.getReplacer());
      }
   },
   MOBILE_PHONE {
      public String apply(SensitiveWrapper wrapper) {
         return SensitiveUtils.mobilePhone(wrapper.getFieldValue(), wrapper.getReplacer());
      }
   },
   ADDRESS {
      public String apply(SensitiveWrapper wrapper) {
         return SensitiveUtils.address(wrapper.getFieldValue(), 8, wrapper.getReplacer());
      }
   },
   EMAIL {
      public String apply(SensitiveWrapper wrapper) {
         return SensitiveUtils.email(wrapper.getFieldValue(), wrapper.getReplacer());
      }
   },
   PASSWORD {
      public String apply(SensitiveWrapper wrapper) {
         return SensitiveUtils.password(wrapper.getFieldValue(), wrapper.getReplacer());
      }
   },
   CAR_LICENSE {
      public String apply(SensitiveWrapper wrapper) {
         return SensitiveUtils.carLicense(wrapper.getFieldValue(), wrapper.getReplacer());
      }
   },
   BANK_CARD {
      public String apply(SensitiveWrapper wrapper) {
         return SensitiveUtils.bankCard(wrapper.getFieldValue(), wrapper.getReplacer());
      }
   },
   CUSTOMIZE_FILTER_WORDS {
      public String apply(SensitiveWrapper wrapper) {
         String fieldValue = wrapper.getFieldValue();
         String fieldName = wrapper.getFieldName();
         Object object = wrapper.getObject();
         Field field = ReflectUtil.getField(object.getClass(), fieldName);
         SensitiveFilterWords filterWords = (SensitiveFilterWords)field.getAnnotation(SensitiveFilterWords.class);
         if (ObjectUtils.isEmpty(filterWords)) {
            LogUtils.warn("{} is marked CUSTOMIZE_FILTER_WORDS strategy, but not has @SensitiveFilterWords, will ignore sensitive it.", new Object[]{field.getName()});
            return fieldValue;
         } else {
            char replacer = wrapper.getReplacer();
            String[] words = filterWords.value();
            if (!ObjectUtils.isEmpty(words)) {
               for(String filterWord : words) {
                  if (fieldValue.contains(filterWord)) {
                     String replacers = CharSequenceUtil.repeat(replacer, filterWord.length());
                     fieldValue = fieldValue.replace(filterWord, replacers);
                  }
               }
            }

            return fieldValue;
         }
      }
   },
   CUSTOMIZE_KEEP_LENGTH {
      public String apply(SensitiveWrapper wrapper) {
         String fieldValue = wrapper.getFieldValue();
         String fieldName = wrapper.getFieldName();
         Object object = wrapper.getObject();
         Field field = ReflectUtil.getField(object.getClass(), fieldName);
         SensitiveKeepLength sensitiveKeepLength = (SensitiveKeepLength)field.getAnnotation(SensitiveKeepLength.class);
         int preKeep = sensitiveKeepLength.preKeep();
         int postKeep = sensitiveKeepLength.postKeep();
         Assert.isTrue(preKeep >= -1, "preKeep must greater than -1");
         Assert.isTrue(postKeep >= -1, "postKeep must greater than -1");
         boolean ignorePreKeep = preKeep <= 0;
         boolean ignoreSuffixKeep = postKeep <= 0;
         if (ignorePreKeep && ignoreSuffixKeep) {
            return fieldValue;
         } else {
            char replacer = wrapper.getReplacer();
            return CharSequenceUtil.replaceByCodePoint(fieldValue, preKeep, fieldValue.length() - postKeep, replacer);
         }
      }
   },
   CUSTOMIZE_HANDLER {
      public String apply(SensitiveWrapper wrapper) {
         String fieldName = wrapper.getFieldName();
         Object object = wrapper.getObject();
         Field field = ReflectUtil.getField(object.getClass(), fieldName);
         SensitiveHandler customizeHandler = (SensitiveHandler)field.getAnnotation(SensitiveHandler.class);
         Class<? extends CustomizeSensitiveHandler> handlerClass = customizeHandler.value();
         CustomizeSensitiveHandler handler = (CustomizeSensitiveHandler)ReflectUtil.newInstance(handlerClass, new Object[0]);
         return handler.customize(wrapper);
      }
   };

   private SensitiveStrategy() {
   }

   public abstract String apply(SensitiveWrapper wrapper);

   // $FF: synthetic method
   private static SensitiveStrategy[] $values() {
      return new SensitiveStrategy[]{CHINESE_NAME, ID_CARD, FIXED_PHONE, MOBILE_PHONE, ADDRESS, EMAIL, PASSWORD, CAR_LICENSE, BANK_CARD, CUSTOMIZE_FILTER_WORDS, CUSTOMIZE_KEEP_LENGTH, CUSTOMIZE_HANDLER};
   }
}
