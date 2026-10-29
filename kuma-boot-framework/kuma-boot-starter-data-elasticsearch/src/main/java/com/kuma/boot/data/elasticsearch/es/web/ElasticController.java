package com.kuma.boot.data.elasticsearch.es.web;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.elasticsearch.es.bean.School;
import com.kuma.boot.data.elasticsearch.es.bean.Student;
import com.kuma.boot.data.elasticsearch.es.utils.ElasticUtil;
import com.kuma.boot.data.elasticsearch.es.utils.EsDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElasticController {
   @Autowired
   private ElasticUtil elasticUtil;

   public ElasticController() {
   }

   @RequestMapping({"/create"})
   public void create() {
      try {
         this.elasticUtil.createIndex(Student.class);
      } catch (Exception e) {
         LogUtils.error(e);
      }

   }

   @RequestMapping({"/add"})
   public void add() {
      try {
         Student student = new Student();
         student.setId("123");
         student.setName("小阿飞");
         student.setBirthday("2005-04-11 12:03:00");
         student.setAddress("上海闵行");
         School school = new School();
         school.setName("上海市闵行小学");
         school.setAddres("上海闵行");
         student.setSchool(school);
         EsDoc<Student> doc = new EsDoc<Student>(student.getId(), student, "student");
         this.elasticUtil.addDocument(doc);
      } catch (Exception e) {
         LogUtils.error(e);
      }

   }
}
