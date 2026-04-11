package com.kuma.boot.translation.translationextension.domain;

import com.kuma.boot.translation.translationextension.annotaion.Translate;
import com.kuma.boot.translation.translationextension.annotaion.TranslationResult;
import java.lang.reflect.Field;

public class ConvertInfo {
   private String name;
   private Field field;
   private Translate translate;
   private TranslationResult translationResult;

   public ConvertInfo() {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Field getField() {
      return this.field;
   }

   public void setField(Field field) {
      this.field = field;
   }

   public Translate getTranslate() {
      return this.translate;
   }

   public void setTranslate(Translate translate) {
      this.translate = translate;
   }

   public TranslationResult getTranslationResult() {
      return this.translationResult;
   }

   public void setTranslationResult(TranslationResult translationResult) {
      this.translationResult = translationResult;
   }
}
