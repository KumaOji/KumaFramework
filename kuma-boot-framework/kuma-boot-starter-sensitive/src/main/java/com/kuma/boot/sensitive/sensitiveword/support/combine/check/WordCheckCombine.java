package com.kuma.boot.sensitive.sensitiveword.support.combine.check;

import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import java.util.ArrayList;
import java.util.List;

public class WordCheckCombine extends AbstractWordCheckCombine {
   public WordCheckCombine() {
   }

   protected List<IWordCheck> getWordCheckList(IWordContext context) {
      List<IWordCheck> wordCheckList = new ArrayList();
      if (context.enableWordCheck()) {
         wordCheckList.add(context.wordCheckWord());
      }

      if (context.enableNumCheck()) {
         wordCheckList.add(context.wordCheckNum());
      }

      if (context.enableEmailCheck()) {
         wordCheckList.add(context.wordCheckEmail());
      }

      if (context.enableUrlCheck()) {
         wordCheckList.add(context.wordCheckUrl());
      }

      if (context.enableIpv4Check()) {
         wordCheckList.add(context.wordCheckIpv4());
      }

      return wordCheckList;
   }
}
