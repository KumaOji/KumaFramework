package com.kuma.boot.sensitive.sensitiveword.api;

import java.util.Set;

public interface IWordTag {
   Set<String> getTag(final String word);
}
