package com.kuma.boot.sensitive.sensitiveword.api;

import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;

public interface ISensitiveWordCharIgnore {
   boolean ignore(final int ix, final char[] chars, InnerSensitiveWordContext innerContext);
}
