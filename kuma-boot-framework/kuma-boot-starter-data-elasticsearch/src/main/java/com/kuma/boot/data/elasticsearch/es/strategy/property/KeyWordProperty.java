package com.kuma.boot.data.elasticsearch.es.strategy.property;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.util.ObjectBuilder;
import com.kuma.boot.data.elasticsearch.es.annotation.EsField;
import com.kuma.boot.data.elasticsearch.es.utils.ElasticUtil;
import java.lang.reflect.Field;
import org.springframework.stereotype.Component;

@Component
public class KeyWordProperty implements PropertyStratgy {
   public KeyWordProperty() {
   }

   public String getType() {
      return "keyword";
   }

   @SuppressWarnings("unchecked")
   public Property handleProperty(EsField annotation, Field field, ElasticUtil elasticUtil) {
      return Property.of((p) -> p.keyword((k) -> (ObjectBuilder)k.ignoreAbove(256)));
   }
}
