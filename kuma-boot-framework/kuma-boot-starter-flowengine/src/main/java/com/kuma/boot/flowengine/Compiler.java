package com.kuma.boot.flowengine;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicLong;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;

public class Compiler {
   public static final String PROXY_PREFIX = "AppKitGen";
   public static final int PID = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
   public static final AtomicLong counter;

   private Compiler() {
   }

   public static Compiler getInstance() {
      return Compiler.CompilerHolder.INSTANCE;
   }

   public <T> Class<T> compilerJavaCode(String className, String src) {
      try {
         SimpleCompiler compiler = new SimpleCompiler();
         compiler.cook(src);
         Class<T> targetType = compiler.getClassLoader().loadClass(className);
         return targetType;
      } catch (ClassNotFoundException | CompileException e) {
         throw new RuntimeException(String.format("\u52a8\u6001\u7f16\u8bd1\u51fa\u9519,className=%s src=%s", className, src), e);
      }
   }

   public CtClass newCtClass(Class supperclass) {
      return this.newCtClassWithClasspath(supperclass, (String)null);
   }

   public CtClass newCtClassWithClasspath(Class supperClass, String path) {
      try {
         ClassPool classPool = new ClassPool(true);
         if (null != path) {
            classPool.insertClassPath(path);
         } else {
            ClassPath classPath = new ClassClassPath(this.getClass());
            classPool.insertClassPath(classPath);
         }

         String dynamicClassName = genClassNameWithPath(supperClass, path);
         CtClass ctClass = classPool.makeClass(dynamicClassName);
         if (supperClass != null & supperClass != Object.class) {
            if (supperClass.isInterface()) {
               ctClass.setInterfaces(new CtClass[]{classPool.get(supperClass.getName())});
            } else {
               ctClass.setSuperclass(classPool.get(supperClass.getName()));
            }
         }

         return ctClass;
      } catch (CannotCompileException | NotFoundException e) {
         throw new RuntimeException(String.format("\u521b\u5efaCtclass\u8fc7\u7a0b\u4e2d\u51fa\u9519supperclass = %s", supperClass), e);
      }
   }

   public Compiler methodWeave(CtClass ctclass, Class supperclass, String src) {
      try {
         ctclass.addMethod(CtNewMethod.make(src, ctclass));
         return this;
      } catch (CannotCompileException e) {
         throw new RuntimeException(String.format("\u65b9\u6cd5\u7ec7\u5165\u8fc7\u7a0b\u4e2d\u51fa\u73b0\u9519\u8befsupperClass =%s,methodDefinition =%s", supperclass, src), e);
      }
   }

   public <T> T newInstance(CtClass ctclass, Class<?>[] parameterTypes, Object[] parameters) {
      try {
         T target;
         if (parameterTypes != null && parameterTypes.length != 0) {
            Constructor<T> constructor = ctclass.toClass().getDeclaredConstructor(parameterTypes);
            target = (T)constructor.newInstance(parameters);
         } else {
            target = (T)ctclass.toClass().newInstance();
         }

         return target;
      } catch (SecurityException | CannotCompileException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
         throw new RuntimeException(String.format("\u6784\u5efa\u8fc7\u7a0b\u4e2d\u51fa\u73b0\u9519\u8befctclass=%s", ctclass), e);
      }
   }

   public void constructImplement(CtClass ctclass, String constructor) {
      try {
         ctclass.addConstructor(CtNewConstructor.make(constructor, ctclass));
      } catch (CannotCompileException e) {
         throw new RuntimeException(String.format("\u6784\u5efa\u8fc7\u7a0b\u4e2d\u51fa\u73b0\u9519\u8befclass =%s,constructorDefinition=%s", ctclass, constructor), e);
      }
   }

   public void filedLeave(CtClass ctclass, String field) {
      this.filedWeaveWithAnnotation(ctclass, field, (String)null);
   }

   public void filedWeaveWithAnnotation(CtClass ctclass, String field, String annotation) {
      try {
         CtField ctField = CtField.make(field, ctclass);
         if (null != annotation) {
            ConstPool constPool = ctclass.getClassFile().getConstPool();
            AnnotationsAttribute attributeInfo = new AnnotationsAttribute(constPool, "RuntimeVisibleAnnotations");
            attributeInfo.addAnnotation(new Annotation(annotation, constPool));
            ctField.getFieldInfo().addAttribute(attributeInfo);
            ctclass.addField(ctField);
         }

      } catch (CannotCompileException e) {
         throw new RuntimeException(String.format("\u6784\u5efafield\u5931\u8d25.field:%s,ctclass:%s", field, ctclass.getName()), e);
      }
   }

   public static <T> String genClassName(Class<T> supperClass) {
      return genClassNameWithPath(supperClass, (String)null);
   }

   public static <T> String genClassNameWithPath(Class<T> supperclass, String path) {
      StringBuilder className = new StringBuilder();
      className.append(null != path ? path : "com.kuma.boot.common.kit.").append("AppKitGen").append(supperclass.getSimpleName()).append(counter());
      return className.toString();
   }

   public static long counter() {
      return counter.getAndAdd(1L);
   }

   static {
      long initializerCounter = System.currentTimeMillis();
      initializerCounter = initializerCounter << 24 >> 8 & Long.MAX_VALUE;
      initializerCounter |= (long)(PID << 25 >> 1 & Integer.MAX_VALUE);
      counter = new AtomicLong(initializerCounter);
   }

   private static class CompilerHolder {
      private static final Compiler INSTANCE = new Compiler();

      private CompilerHolder() {
      }
   }
}
