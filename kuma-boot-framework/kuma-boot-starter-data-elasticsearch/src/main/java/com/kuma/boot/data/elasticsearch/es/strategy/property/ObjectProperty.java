package com.kuma.boot.data.elasticsearch.es.strategy.property;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import com.kuma.boot.data.elasticsearch.es.annotation.EsField;
import com.kuma.boot.data.elasticsearch.es.utils.ElasticUtil;
import java.lang.reflect.Field;
import org.springframework.stereotype.Component;

@Component
public class ObjectProperty implements PropertyStratgy {
   public ObjectProperty() {
   }

   public String getType() {
      return "object";
   }

   public Property handleProperty(EsField annotation, Field field, ElasticUtil elasticUtil) {
      return Property.of((p) -> p.object((o) -> {
            o.properties(elasticUtil.handleProperties(field.getType().getDeclaredFields()));
            return o;
         }));
   }
}
