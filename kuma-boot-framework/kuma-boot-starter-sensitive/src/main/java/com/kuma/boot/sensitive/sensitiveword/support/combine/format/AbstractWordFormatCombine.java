package com.kuma.boot.sensitive.sensitiveword.support.combine.format;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;
import com.kuma.boot.sensitive.sensitiveword.api.combine.IWordFormatCombine;
import com.kuma.boot.sensitive.sensitiveword.support.format.WordFormats;
import java.util.Collection;
import java.util.List;

public abstract class AbstractWordFormatCombine implements IWordFormatCombine {
   public AbstractWordFormatCombine() {
   }

   protected abstract List<IWordFormat> getWordFormatList(IWordContext context);

   public IWordFormat initWordFormat(IWordContext context) {
      List<IWordFormat> list = this.getWordFormatList(context);
      return WordFormats.chains((Collection)list);
   }
}
