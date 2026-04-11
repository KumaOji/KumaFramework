package com.kuma.boot.sensitive.sensitivelog.api;

import java.lang.reflect.Field;
import java.util.List;

public interface Context {
   List<Field> getAllFieldList();

   Field getCurrentField();

   String getCurrentFieldName();

   Object getCurrentFieldValue();

   Object getCurrentObject();

   Class getBeanClass();

   Object getEntry();
}
