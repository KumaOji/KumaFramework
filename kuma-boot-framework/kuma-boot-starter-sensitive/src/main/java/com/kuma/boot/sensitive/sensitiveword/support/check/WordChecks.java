package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import java.util.Collection;

public final class WordChecks {
   private WordChecks() {
   }

   public static IWordCheck chains(final IWordCheck... sensitiveChecks) {
      return (IWordCheck)(ArrayUtils.isEmpty(sensitiveChecks) ? none() : new WordCheckInit() {
         protected void init(Pipeline<IWordCheck> pipeline) {
            for(IWordCheck check : sensitiveChecks) {
               pipeline.addLast(check);
            }

         }
      });
   }

   public static IWordCheck chains(final Collection<IWordCheck> sensitiveChecks) {
      return (IWordCheck)(CollectionUtils.isEmpty(sensitiveChecks) ? none() : new WordCheckInit() {
         protected void init(Pipeline<IWordCheck> pipeline) {
            for(IWordCheck check : sensitiveChecks) {
               pipeline.addLast(check);
            }

         }
      });
   }

   public static IWordCheck email() {
      return WordCheckEmail.getInstance();
   }

   public static IWordCheck num() {
      return WordCheckNum.getInstance();
   }

   public static IWordCheck url() {
      return WordCheckUrl.getInstance();
   }

   public static IWordCheck word() {
      return WordCheckWord.getInstance();
   }

   public static IWordCheck none() {
      return WordCheckNone.getInstance();
   }

   public static IWordCheck ipv4() {
      return WordCheckIPV4.getInstance();
   }

   public static IWordCheck urlNoPrefix() {
      return WordCheckUrlNoPrefix.getInstance();
   }
}
