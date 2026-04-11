package com.kuma.boot.sensitive.sensitiveword.support.combine.allowdeny;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordAllowDenyCombine extends AbstractWordAllowDenyCombine {
   public WordAllowDenyCombine() {
   }

   protected Collection<String> doGetActualDenyList(List<String> allowList, List<String> denyList, IWordContext context) {
      Set<String> resultSet = new HashSet(denyList.size());
      Set<String> allowSet = new HashSet(allowList);

      for(String deny : denyList) {
         if (!allowSet.contains(deny)) {
            resultSet.add(deny);
         }
      }

      return resultSet;
   }
}
