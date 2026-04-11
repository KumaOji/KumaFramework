package com.kuma.boot.data.elasticsearch.es.strategy.property;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import com.kuma.boot.data.elasticsearch.es.annotation.EsField;
import com.kuma.boot.data.elasticsearch.es.utils.ElasticUtil;
import java.lang.reflect.Field;
import org.springframework.stereotype.Component;

@Component
public class DateProperty implements PropertyStratgy {
   public DateProperty() {
   }

   public String getType() {
      return "date";
   }

   public Property handleProperty(EsField annotation, Field field, ElasticUtil elasticUtil) {
      return Property.of((p) -> p.date((d) -> d.format("yyyy-MM-dd HH:mm:ss").index(true)));
   }
}
