package com.kuma.boot.sensitive.sensitiveword.support.tag;

import com.kuma.boot.common.support.pipeline.DefaultPipeline;
import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.sensitive.sensitiveword.api.IWordTag;
import com.xkzhangsan.time.utils.CollectionUtil;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractWordTagInit extends AbstractWordTag {
   public AbstractWordTagInit() {
   }

   protected abstract void init(final Pipeline<IWordTag> pipeline);

   public Set<String> doGetTag(String word) {
      Pipeline<IWordTag> pipeline = new DefaultPipeline();
      this.init(pipeline);
      Set<String> resultSet = new HashSet();

      for(IWordTag wordTag : pipeline.list()) {
         Set<String> tempTagSet = wordTag.getTag(word);
         if (CollectionUtil.isNotEmpty(tempTagSet)) {
            resultSet.addAll(tempTagSet);
         }
      }

      return resultSet;
   }
}
