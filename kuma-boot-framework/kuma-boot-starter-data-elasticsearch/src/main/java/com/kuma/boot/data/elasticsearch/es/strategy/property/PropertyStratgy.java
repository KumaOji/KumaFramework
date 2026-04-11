package com.kuma.boot.data.elasticsearch.es.strategy.property;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import com.kuma.boot.data.elasticsearch.es.annotation.EsField;
import com.kuma.boot.data.elasticsearch.es.utils.ElasticUtil;
import java.lang.reflect.Field;

public interface PropertyStratgy {
   String getType();

   Property handleProperty(EsField annotation, Field field, ElasticUtil elasticUtil);
}
