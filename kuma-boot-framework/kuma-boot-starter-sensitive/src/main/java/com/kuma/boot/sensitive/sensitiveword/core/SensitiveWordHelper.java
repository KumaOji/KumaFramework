package com.kuma.boot.sensitive.sensitiveword.core;

import com.kuma.boot.sensitive.sensitiveword.api.IWordReplace;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResultHandler;
import com.kuma.boot.sensitive.sensitiveword.bs.SensitiveWordBs;
import com.kuma.boot.sensitive.sensitiveword.support.replace.WordReplaces;
import java.util.List;

public final class SensitiveWordHelper {
   private static final SensitiveWordBs WORD_BS = SensitiveWordBs.newInstance().enableNumCheck(false).enableEmailCheck(false).enableUrlCheck(false).init();

   private SensitiveWordHelper() {
   }

   public static boolean contains(final String target) {
      return WORD_BS.contains(target);
   }

   public static List<String> findAll(final String target) {
      return WORD_BS.findAll(target);
   }

   public static String findFirst(final String target) {
      return WORD_BS.findFirst(target);
   }

   public static String replace(final String target, final IWordReplace replace) {
      SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance().wordReplace(replace).init();
      return sensitiveWordBs.replace(target);
   }

   public static String replace(final String target, final char replaceChar) {
      IWordReplace replace = WordReplaces.chars(replaceChar);
      return replace(target, replace);
   }

   public static String replace(final String target) {
      return WORD_BS.replace(target);
   }

   public static <R> List<R> findAll(final String target, final IWordResultHandler<R> handler) {
      return WORD_BS.<R>findAll(target, handler);
   }

   public static <R> R findFirst(final String target, final IWordResultHandler<R> handler) {
      return (R)WORD_BS.findFirst(target, handler);
   }
}
