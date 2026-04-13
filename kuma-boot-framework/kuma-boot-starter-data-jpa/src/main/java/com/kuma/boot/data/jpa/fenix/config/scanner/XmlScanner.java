package com.kuma.boot.data.jpa.fenix.config.scanner;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.fenix.exception.ConfigNotFoundException;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.helper.XmlNodeHelper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

public class XmlScanner {
   private static final String DIR_XML_PATTERN = "**/*.xml";

   public XmlScanner() {
   }

   public Map<String, XmlResource> scan(String xmlLocations) {
      Map<String, XmlResource> xmlResourceMap = new HashMap();
      if (StringHelper.isBlank(xmlLocations)) {
         return xmlResourceMap;
      } else {
         String[] xmlLocationArr = xmlLocations.split(",");
         if (LogUtils.isDebugEnabled()) {
            LogUtils.debug("\u3010Fenix \u63d0\u793a\u3011\u5c06\u626b\u63cf\u8fd9\u4e9b\u4f4d\u7f6e\u7684 Fenix XML \u6587\u4ef6\uff1a\u3010{}\u3011", new Object[]{Arrays.asList(xmlLocationArr)});
         }

         for(String xmlLocation : xmlLocationArr) {
            if (!StringHelper.isBlank(xmlLocation)) {
               String location = xmlLocation.trim();
               if (StringHelper.isXmlFile(location)) {
                  this.buildXmlResourcesByLocation(xmlResourceMap, location);
               } else {
                  location = location.replace(".", "/");
                  location = location.endsWith("/") ? location : location + "/";
                  this.buildXmlResourcesByLocation(xmlResourceMap, location + "**/*.xml");
               }
            }
         }

         return xmlResourceMap;
      }
   }

   private void buildXmlResourcesByLocation(Map<String, XmlResource> xmlResourceMap, String location) {
      Resource[] resources = this.getResourcesByLocation(location);

      try {
         for(Resource resource : resources) {
            URL url = resource.getURL();
            String path = url.getPath();
            if (xmlResourceMap.containsKey(path)) {
               LogUtils.debug("\u3010Fenix \u63d0\u793a\u3011\u5df2\u7ecf\u626b\u63cf\u8fc7\u4e86\u3010" + path + "\u3011\u6587\u4ef6\uff0c\u5c06\u8df3\u8fc7\u8be5 XML \u6587\u4ef6\u7684\u521d\u59cb\u5316\u52a0\u8f7d.", new Object[0]);
            } else {
               InputStream in = resource.getInputStream();

               try {
                  XmlResource xmlResource = getFenixXmlResource(in, path);
                  if (xmlResource != null) {
                     xmlResourceMap.put(path, xmlResource.setUrl(url));
                  }
               } catch (Throwable var14) {
                  if (in != null) {
                     try {
                        in.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }
                  }

                  throw var14;
               }

               if (in != null) {
                  in.close();
               }
            }
         }

      } catch (IOException e) {
         throw new FenixException("\u3010Fenix \u5f02\u5e38\u3011\u521d\u59cb\u5316\u8bfb\u53d6\u3010" + location + "\u3011\u4e0b\u7684 Fenix XML \u6587\u4ef6\u51fa\u9519\uff0c\u8bf7\u68c0\u67e5\uff01", e);
      }
   }

   private Resource[] getResourcesByLocation(String location) {
      try {
         return ResourcePatternUtils.getResourcePatternResolver(new PathMatchingResourcePatternResolver()).getResources(location);
      } catch (IOException expected) {
         LogUtils.warn("\u3010Fenix \u8b66\u793a\u3011\u672a\u67e5\u627e\u5230\u5339\u914d\u89c4\u5219\u3010" + location + "\u3011\u4e0b\u7684 Fenix XML \u6587\u4ef6.", new Object[]{expected.getMessage()});
         return new Resource[0];
      }
   }

   public static XmlResource getFenixXmlResource(InputStream in, String path) {
      Document doc;
      try {
         doc = (new SAXReader()).read(in);
      } catch (Exception var5) {
         LogUtils.info("\u3010Fenix \u63d0\u793a\u3011\u89e3\u6790\u8def\u5f84\u4e3a:\u3010" + path + "\u3011\u7684 Fenix XML \u6587\u4ef6\u5f02\u5e38\uff0c\u5c06\u5ffd\u7565\u6b64\u6587\u4ef6!", new Object[0]);
         return null;
      }

      Node root = doc.getRootElement();
      if (root != null && "fenixs".equals(root.getName())) {
         String namespace = XmlNodeHelper.getNodeText(root.selectSingleNode("attribute::namespace"));
         if (StringHelper.isBlank(namespace)) {
            throw new ConfigNotFoundException("\u3010Fenix \u8b66\u793a\u3011Fenix XML \u6587\u4ef6 " + path + " \u7684\u6839\u8282\u70b9 namespace \u547d\u540d\u7a7a\u95f4\u5c5e\u6027\u672a\u914d\u7f6e\uff0c\u8bf7\u914d\u7f6e!");
         } else {
            return new XmlResource(namespace, doc);
         }
      } else {
         return null;
      }
   }
}
