package com.kuma.boot.translation.translationextension.handler;

import com.kuma.boot.translation.translationextension.annotaion.TranslationResult;
import java.lang.reflect.Type;

public interface TranslationHandler {
   boolean adaptation(Type type);

   void translation(Object object, Type type, TranslationResult translationResult);
}
