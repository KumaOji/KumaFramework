package com.kuma.boot.data.elasticsearch.es.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kuma.boot.data.elasticsearch.es.annotation.AnalyzerType;
import com.kuma.boot.data.elasticsearch.es.annotation.EsBean;
import com.kuma.boot.data.elasticsearch.es.annotation.EsField;
import com.kuma.boot.data.elasticsearch.es.annotation.FieldType;

@EsBean(
   index = "student",
   shards = "3",
   replicas = "1"
)
public class Student {
   @EsField(
      type = FieldType.KEYWORD
   )
   private String id;
   @EsField(
      type = FieldType.KEYWORD
   )
   private String name;
   @EsField(
      type = FieldType.INTEGER
   )
   private Integer age;
   @EsField(
      type = FieldType.OBJECT
   )
   private School school;
   @EsField(
      type = FieldType.TEXT,
      analyzer = AnalyzerType.IK_SMART,
      searchAnalyzer = AnalyzerType.IK_MAX_WORD
   )
   private String address;
   @EsField(
      type = FieldType.DATE
   )
   @JsonFormat(
      pattern = "yyyy-MM-dd HH:mm:ss",
      timezone = "GMT+8"
   )
   private String birthday;

   public Student() {
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Integer getAge() {
      return this.age;
   }

   public void setAge(Integer age) {
      this.age = age;
   }

   public School getSchool() {
      return this.school;
   }

   public void setSchool(School school) {
      this.school = school;
   }

   public String getAddress() {
      return this.address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public String getBirthday() {
      return this.birthday;
   }

   public void setBirthday(String birthday) {
      this.birthday = birthday;
   }
}
