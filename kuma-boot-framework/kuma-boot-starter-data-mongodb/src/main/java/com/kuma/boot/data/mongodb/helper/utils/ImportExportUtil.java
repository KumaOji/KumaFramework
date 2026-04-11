package com.kuma.boot.data.mongodb.helper.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mongodb.helper.bean.Page;
import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

@AutoConfiguration
public class ImportExportUtil {
   @Autowired
   private MongoTemplate mongoTemplate;
   @Autowired
   private MongoHelper mongoHelper;

   public ImportExportUtil() {
   }

   public void exportDb(String path) {
      path = path.replace(".zip", "");
      FileUtil.del(path);
      FileUtil.del(path + ".zip");

      try {
         Set<Class<?>> set = ClassUtil.scanPackage(SystemTool.deduceMainApplicationClassName());
         Page<?> page = new Page();
         page.setLimit(1000);

         for(Class<?> clazz : set) {
            Document document = (Document)clazz.getAnnotation(Document.class);
            if (document != null) {
               page.setCurr(1);

               while(true) {
                  page = this.mongoHelper.findPage(page, clazz);
                  if (page.getList().size() == 0) {
                     break;
                  }

                  List<String> lines = new ArrayList();

                  for(Object object : page.getList()) {
                     lines.add(JSONUtil.toJsonStr(object));
                  }

                  FileUtil.appendLines(lines, path + File.separator + clazz.getSimpleName() + ".json", StandardCharsets.UTF_8);
                  LogUtils.info(clazz.getSimpleName() + "\u8868\u5bfc\u51fa\u4e86" + page.getList().size() + "\u6761\u6570\u636e", new Object[0]);
                  page.setCurr(page.getCurr() + 1);
               }
            }
         }

         ZipUtil.zip(path);
      } catch (Exception e) {
         LogUtils.error(e);
         FileUtil.del(path + ".zip");
      }

      FileUtil.del(path);
   }

   public void importDb(String path) {
      if (!FileUtil.exist(path)) {
         LogUtils.info(path + "\u6587\u4ef6\u4e0d\u5b58\u5728", new Object[0]);
      } else {
         BufferedReader reader = null;
         path = path.replace(".zip", "");
         FileUtil.del(path);
         ZipUtil.unzip(path + ".zip");

         try {
            for(Class<?> clazz : ClassUtil.scanPackage(SystemTool.deduceMainApplicationClassName())) {
               Document document = (Document)clazz.getAnnotation(Document.class);
               if (document != null) {
                  File file = new File(path + File.separator + clazz.getSimpleName() + ".json");
                  if (file.exists()) {
                     this.mongoTemplate.dropCollection(clazz);
                     reader = FileUtil.getReader(file, StandardCharsets.UTF_8);
                     List<Object> list = new ArrayList();

                     while(true) {
                        String json = reader.readLine();
                        if (StrUtil.isEmpty(json)) {
                           this.mongoTemplate.insertAll(list);
                           LogUtils.info(clazz.getSimpleName() + "\u8868\u5bfc\u5165\u4e86" + list.size() + "\u6761\u6570\u636e", new Object[0]);
                           list.clear();
                           break;
                        }

                        list.add(JSONUtil.toBean(json, clazz));
                        if (list.size() == 1000) {
                           this.mongoTemplate.insertAll(list);
                           LogUtils.info(clazz.getSimpleName() + "\u8868\u5bfc\u5165\u4e86" + list.size() + "\u6761\u6570\u636e", new Object[0]);
                           list.clear();
                        }
                     }
                  }
               }
            }
         } catch (Exception e) {
            LogUtils.error(e);
         } finally {
            IoUtil.close(reader);
         }

         FileUtil.del(path);
      }
   }
}
