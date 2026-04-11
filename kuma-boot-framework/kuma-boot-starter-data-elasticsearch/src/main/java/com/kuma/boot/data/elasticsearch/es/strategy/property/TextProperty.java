package com.kuma.boot.data.elasticsearch.es.strategy.property;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import com.kuma.boot.data.elasticsearch.es.annotation.EsField;
import com.kuma.boot.data.elasticsearch.es.utils.ElasticUtil;
import java.lang.reflect.Field;
import org.springframework.stereotype.Component;

@Component
public class TextProperty implements PropertyStratgy {
   public TextProperty() {
   }

   public String getType() {
      return "text";
   }

   public Property handleProperty(EsField annotation, Field field, ElasticUtil elasticUtil) {
      return Property.of((p) -> p.text((t) -> t.searchAnalyzer(annotation.searchAnalyzer().getType()).analyzer(annotation.analyzer().getType()).index(true)));
   }
}
