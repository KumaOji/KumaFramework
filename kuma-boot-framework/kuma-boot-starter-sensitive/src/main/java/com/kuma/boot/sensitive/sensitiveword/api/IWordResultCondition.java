package com.kuma.boot.sensitive.sensitiveword.api;

import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordValidModeEnum;

public interface IWordResultCondition {
   boolean match(final IWordResult wordResult, final String text, final WordValidModeEnum modeEnum, final IWordContext context);
}
