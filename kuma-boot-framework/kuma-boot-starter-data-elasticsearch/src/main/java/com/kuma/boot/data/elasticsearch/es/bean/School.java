package com.kuma.boot.data.elasticsearch.es.bean;

import com.kuma.boot.data.elasticsearch.es.annotation.EsBean;
import com.kuma.boot.data.elasticsearch.es.annotation.EsField;
import com.kuma.boot.data.elasticsearch.es.annotation.FieldType;

@EsBean(
   index = "school"
)
public class School {
   @EsField(
      type = FieldType.KEYWORD
   )
   private String name;
   @EsField(
      type = FieldType.KEYWORD
   )
   private String addres;

   public School() {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getAddres() {
      return this.addres;
   }

   public void setAddres(String addres) {
      this.addres = addres;
   }
}
