package com.kuma.boot.data.jpa.fenix.config.scanner;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.fenix.config.FenixConfig;
import com.kuma.boot.data.jpa.fenix.config.annotation.Tagger;
import com.kuma.boot.data.jpa.fenix.config.annotation.Taggers;
import com.kuma.boot.data.jpa.fenix.config.entity.TagHandler;
import com.kuma.boot.data.jpa.fenix.core.FenixHandler;
import com.kuma.boot.data.jpa.fenix.helper.CollectionHelper;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class TaggerScanner {
   private static final String FILE_PROTOCOL = "file";
   private final Set<Class<?>> classSet = new HashSet();

   public TaggerScanner() {
   }

   public void scan(String handlerLocations) {
      if (!StringHelper.isBlank(handlerLocations)) {
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         String[] locationArr = handlerLocations.split(",");

         for(String location : locationArr) {
            if (!StringHelper.isBlank(location)) {
               String handlerLocation = location.trim();
               if (!StringHelper.isJavaFile(handlerLocation) && !StringHelper.isClassFile(handlerLocation)) {
                  this.addClassByPackage(classLoader, handlerLocation);
               } else {
                  this.addClassByName(classLoader, handlerLocation.substring(0, handlerLocation.lastIndexOf(".")));
               }
            }
         }

         this.addTagHanderInMap();
      }
   }

   private void addClassByName(ClassLoader classLoader, String className) {
      try {
         this.classSet.add(classLoader.loadClass(className));
      } catch (ClassNotFoundException var4) {
         LogUtils.warn("\u3010Fenix \u8b66\u544a\u3011\u672a\u627e\u5230 class \u7c7b:\u3010" + className + "\u3011\uff0c\u5c06\u5ffd\u7565\u4e0d\u89e3\u6790\u6b64\u7c7b.", new Object[0]);
      }

   }

   private void addClassByPackage(ClassLoader classLoader, String packageName) {
      String packageDirName = packageName.replace('.', '/');
      Enumeration<URL> urlEnum = this.getUrlsByPackge(classLoader, packageDirName);
      if (urlEnum != null) {
         while(urlEnum.hasMoreElements()) {
            URL url = (URL)urlEnum.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
               try {
                  this.addClassesByFile(classLoader, packageName, URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.toString()));
               } catch (UnsupportedEncodingException var8) {
                  LogUtils.warn("\u3010Fenix \u8b66\u544a\u3011\u8be5\u5305\u7ed3\u6784\u65e0\u6cd5\u8f6c\u6362\u6210 UTF-8 \u7684\u7f16\u7801\u683c\u5f0f.", new Object[0]);
               }
            } else if ("jar".equals(protocol)) {
               this.addClassByJar(classLoader, url, packageName, packageDirName);
            }
         }

      }
   }

   private Enumeration<URL> getUrlsByPackge(ClassLoader classLoader, String packageName) {
      try {
         return classLoader.getResources(packageName);
      } catch (IOException var4) {
         LogUtils.warn("\u3010Fenix \u8b66\u544a\u3011\u672a\u627e\u5230\u5305:\u3010" + packageName + "\u3011\u4e0b\u7684 URL.", new Object[0]);
         return null;
      }
   }

   private void addClassesByFile(ClassLoader classLoader, String packageName, String packagePath) {
      File dir = new File(packagePath);
      if (dir.exists() && dir.isDirectory()) {
         File[] dirFiles = dir.listFiles((filex) -> filex.isDirectory() || StringHelper.isClassFile(filex.getName()));
         if (dirFiles != null && dirFiles.length != 0) {
            for(File file : dirFiles) {
               if (file.isDirectory()) {
                  this.addClassesByFile(classLoader, packageName + "." + file.getName(), file.getAbsolutePath());
               } else {
                  this.addClassByName(classLoader, packageName + "." + file.getName().substring(0, file.getName().lastIndexOf(46)));
               }
            }

         }
      }
   }

   private void addClassByJar(ClassLoader classLoader, URL url, String packageName, String packageDirName) {
      try {
         JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
         Enumeration<JarEntry> entries = jar.entries();

         while(entries.hasMoreElements()) {
            JarEntry entry = (JarEntry)entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
               name = name.substring(1);
            }

            if (name.startsWith(packageDirName)) {
               int index = name.lastIndexOf(47);
               if (index != -1) {
                  packageName = name.substring(0, index).replace('/', '.');
               }

               if (index != -1 && name.endsWith(".class") && !entry.isDirectory()) {
                  String className = name.substring(packageName.length() + 1, name.length() - 6);
                  this.addClassByName(classLoader, packageName + "." + className);
               }
            }
         }
      } catch (IOException var11) {
         LogUtils.warn("\u3010Fenix \u8b66\u544a\u3011\u4ece jar \u6587\u4ef6\u4e2d\u8bfb\u53d6 class \u51fa\u9519.", new Object[0]);
      }

   }

   private void addTagHanderInMap() {
      for(Class<?> cls : this.classSet) {
         if (cls.isAnnotationPresent(Tagger.class) && this.isImplFenixHandlerClass(cls)) {
            LogUtils.debug("\u3010Fenix \u63d0\u793a\u3011\u626b\u63cf\u5230\u5b9e\u73b0\u4e86 FenixHandler \u63a5\u53e3\uff0c\u4e14\u542b @Tagger \u6ce8\u89e3\u7684\u7c7b\uff1a\u3010{}\u3011", new Object[]{cls.getName()});
            this.addTagHandlerInMapByTagger(cls, (Tagger)cls.getAnnotation(Tagger.class));
         }

         if (cls.isAnnotationPresent(Taggers.class) && this.isImplFenixHandlerClass(cls)) {
            LogUtils.debug("\u3010Fenix \u63d0\u793a\u3011\u626b\u63cf\u5230\u5b9e\u73b0\u4e86 FenixHandler \u63a5\u53e3\uff0c\u4e14\u542b\u591a\u4e2a @Tagger \u6ce8\u89e3\u7684\u7c7b\uff1a\u3010{}\u3011", new Object[]{cls.getName()});
            Tagger[] taggerArr = ((Taggers)cls.getAnnotation(Taggers.class)).value();

            for(Tagger tagger : taggerArr) {
               this.addTagHandlerInMapByTagger(cls, tagger);
            }
         }
      }

   }

   private boolean isImplFenixHandlerClass(Class<?> implCls) {
      Class<?>[] classes = implCls.getInterfaces();
      if (CollectionHelper.isNotEmpty(classes)) {
         for(Class<?> cls : classes) {
            if (FenixHandler.class.isAssignableFrom(cls)) {
               return true;
            }
         }
      }

      return false;
   }

   private void addTagHandlerInMapByTagger(Class<? extends FenixHandler> cls, Tagger tagger) {
      FenixConfig.getTagHandlerMap().put(tagger.value(), new TagHandler(tagger.prefix(), cls, tagger.symbol()));
   }
}
