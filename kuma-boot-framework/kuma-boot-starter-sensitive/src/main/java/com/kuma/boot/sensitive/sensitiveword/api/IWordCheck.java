package com.kuma.boot.sensitive.sensitiveword.api;

import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.support.check.WordCheckResult;

public interface IWordCheck {
   WordCheckResult sensitiveCheck(final int beginIndex, final InnerSensitiveWordContext context);
}
