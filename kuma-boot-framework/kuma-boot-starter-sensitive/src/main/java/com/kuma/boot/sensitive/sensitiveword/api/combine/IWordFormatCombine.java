package com.kuma.boot.sensitive.sensitiveword.api.combine;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;

public interface IWordFormatCombine {
   IWordFormat initWordFormat(final IWordContext context);
}
