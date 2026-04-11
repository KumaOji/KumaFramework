package com.kuma.boot.sensitive.sensitiveword.api.combine;

import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;

public interface IWordCheckCombine {
   IWordCheck initWordCheck(final IWordContext context);
}
