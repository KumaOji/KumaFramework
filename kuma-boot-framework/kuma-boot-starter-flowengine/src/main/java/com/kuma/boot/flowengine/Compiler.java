package com.taotao.boot.flowengine;

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
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;

public class Compiler {

   public static final String PROXY_PREFIX = "AppKitGen";

   public static final int PID = Integer.parseInt(
           ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);


   public static final AtomicLong counter;

   static {
      long initializerCounter = System.currentTimeMillis();

      initializerCounter = ((initializerCounter << 24) >> 8) & Long.MAX_VALUE;
      initializerCounter = initializerCounter | ((((PID << 25) >> 1)) & Integer.MAX_VALUE);
      counter = new AtomicLong(initializerCounter);
   }

   private Compiler() {
   }

   private static class CompilerHolder {

      private static final Compiler INSTANCE = new Compiler();
   }


   public static Compiler getInstance() {
      return CompilerHolder.INSTANCE;
   }

   public <T> Class<T> compilerJavaCode(String className, String src) {
      try {
         SimpleCompiler compiler = new SimpleCompiler();

         compiler.cook(src);
         Class<T> targetType = (Class<T>) compiler.getClassLoader().loadClass(className);
         return targetType;

      }
      catch (CompileException | ClassNotFoundException e) {
         throw new RuntimeException(
                 String.format("动态编译出错,className=%s src=%s", className, src), e);
      }
   }

   public CtClass newCtClass(Class supperclass) {
      return newCtClassWithClasspath(supperclass, null);
   }

   public CtClass newCtClassWithClasspath(Class supperClass, String path) {
      try {
         ClassPool classPool = new ClassPool(true);
         if (null != path) {
            classPool.insertClassPath(path);
         }
         else {
            ClassPath classPath = new ClassClassPath(this.getClass());
            classPool.insertClassPath(classPath);
         }
         String dynamicClassName = genClassNameWithPath(supperClass, path);
         CtClass ctClass = classPool.makeClass(dynamicClassName);
         if (supperClass != null & supperClass != Object.class) {
            if (supperClass.isInterface()) {
               ctClass.setInterfaces(new CtClass[]{classPool.get(supperClass.getName())});
            }
            else {
               ctClass.setSuperclass(classPool.get(supperClass.getName()));
            }
         }
         return ctClass;
      }
      catch (NotFoundException | CannotCompileException e) {
         throw new RuntimeException(
                 String.format("创建Ctclass过程中出错supperclass = %s", supperClass), e);
      }
   }


   public Compiler methodWeave(CtClass ctclass, Class supperclass, String src) {
      try {
         ctclass.addMethod(CtNewMethod.make(src, ctclass));
      }
      catch (CannotCompileException e) {
         throw new RuntimeException(
                 String.format("方法织入过程中出现错误supperClass =%s,methodDefinition =%s",
                         supperclass,
                         src), e);
      }
      return this;
   }

   public <T> T newInstance(CtClass ctclass, Class<?>[] parameterTypes, Object[] parameters) {
      T target;
      try {
         if (parameterTypes == null || parameterTypes.length == 0) {
            target = (T) ctclass.toClass().newInstance();
         }
         else {
            Constructor<T> constructor = (Constructor<T>) ctclass.toClass()
                    .getDeclaredConstructor(parameterTypes);
            target = constructor.newInstance(parameters);
         }
      }
      catch (NoSuchMethodException | SecurityException | CannotCompileException |
             InstantiationException | IllegalAccessException | IllegalArgumentException |
             InvocationTargetException e) {

         throw new RuntimeException(String.format("构建过程中出现错误ctclass=%s", ctclass), e);
      }
      return target;
   }


   public void constructImplement(CtClass ctclass, String constructor) {
      try {
         ctclass.addConstructor(CtNewConstructor.make(constructor, ctclass));
      }
      catch (CannotCompileException e) {
         throw new RuntimeException(
                 String.format("构建过程中出现错误class =%s,constructorDefinition=%s", ctclass,
                         constructor), e);
      }
   }

   public void filedLeave(CtClass ctclass, String field) {
      filedWeaveWithAnnotation(ctclass, field, null);
   }

   public void filedWeaveWithAnnotation(CtClass ctclass, String field, String annotation) {
      try {
         CtField ctField = CtField.make(field, ctclass);
         if (null != annotation) {
            ConstPool constPool = ctclass.getClassFile().getConstPool();
            AnnotationsAttribute attributeInfo = new AnnotationsAttribute(constPool,
                    AnnotationsAttribute.visibleTag);
            attributeInfo.addAnnotation(
                    new javassist.bytecode.annotation.Annotation(annotation, constPool));
            ctField.getFieldInfo().addAttribute(attributeInfo);
            ctclass.addField(ctField);
         }
      }
      catch (CannotCompileException e) {
         throw new RuntimeException(
                 String.format("构建field失败.field:%s,ctclass:%s", field, ctclass.getName()),
                 e);
      }
   }


   public static <T> String genClassName(Class<T> supperClass) {
      return genClassNameWithPath(supperClass, null);
   }

   public static <T> String genClassNameWithPath(Class<T> supperclass, String path) {
      StringBuilder className = new StringBuilder();
      className.append(null != path ? path : "com.taotao.boot.common.kit.").append(PROXY_PREFIX)
              .append(supperclass.getSimpleName()).append(counter());
      return className.toString();
   }

   public static long counter() {
      return counter.getAndAdd(1L);
   }

}
