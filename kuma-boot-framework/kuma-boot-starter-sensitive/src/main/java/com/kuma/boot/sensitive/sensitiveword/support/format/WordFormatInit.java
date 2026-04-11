package com.kuma.boot.sensitive.sensitiveword.support.format;

import com.kuma.boot.common.support.pipeline.DefaultPipeline;
import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;

public abstract class WordFormatInit implements IWordFormat {
   public WordFormatInit() {
   }

   protected abstract void init(final Pipeline<IWordFormat> pipeline);

   public char format(char original, IWordContext context) {
      Pipeline<IWordFormat> pipeline = new DefaultPipeline();
      this.init(pipeline);
      char result = original;

      for(IWordFormat charFormat : pipeline.list()) {
         result = charFormat.format(result, context);
      }

      return result;
   }
}
