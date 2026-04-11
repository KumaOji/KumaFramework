package com.kuma.boot.sensitive.sensitiveword.support.combine.allowdeny;

import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.combine.IWordAllowDenyCombine;
import com.kuma.boot.sensitive.sensitiveword.utils.InnerWordFormatUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractWordAllowDenyCombine implements IWordAllowDenyCombine {
   public AbstractWordAllowDenyCombine() {
   }

   protected abstract Collection<String> doGetActualDenyList(List<String> allowList, List<String> denyList, IWordContext context);

   public Collection<String> getActualDenyList(final List<String> allowList, final List<String> denyList, IWordContext context) {
      List<String> formatAllowList = InnerWordFormatUtils.formatWordList(allowList, context);
      List<String> formatDenyList = InnerWordFormatUtils.formatWordList(denyList, context);
      if (CollectionUtils.isEmpty(formatDenyList)) {
         return Collections.emptyList();
      } else {
         return (Collection<String>)(CollectionUtils.isEmpty(formatAllowList) ? formatDenyList : this.doGetActualDenyList(formatAllowList, formatDenyList, context));
      }
   }
}
