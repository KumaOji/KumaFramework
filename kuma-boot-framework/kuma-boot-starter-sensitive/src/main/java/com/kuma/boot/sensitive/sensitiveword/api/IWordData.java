package com.kuma.boot.sensitive.sensitiveword.api;

import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordContainsTypeEnum;
import java.util.Collection;

public interface IWordData extends ISensitiveWordDestroy {
   void initWordData(Collection<String> collection);

   void removeWord(Collection<String> collection);

   void addWord(Collection<String> collection);

   WordContainsTypeEnum contains(final StringBuilder stringBuilder, final InnerSensitiveWordContext innerContext);
}
