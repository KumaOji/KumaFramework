package com.kuma.boot.sensitive.sensitiveword.support.combine.check;

import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.combine.IWordCheckCombine;
import com.kuma.boot.sensitive.sensitiveword.support.check.WordChecks;
import java.util.Collection;
import java.util.List;

public abstract class AbstractWordCheckCombine implements IWordCheckCombine {
   public AbstractWordCheckCombine() {
   }

   protected abstract List<IWordCheck> getWordCheckList(IWordContext context);

   public IWordCheck initWordCheck(IWordContext context) {
      List<IWordCheck> wordCheckList = this.getWordCheckList(context);
      return WordChecks.chains((Collection)wordCheckList);
   }
}
