package com.kuma.boot.flowengine.easywork.util;

import java.util.Map;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class Checker {
   private Checker() {
      throw new AssertionError("No " + this.getClass().getName() + " instances for you!");
   }

   public static <T> boolean BeNotNull(T reference) {
      return reference != null;
   }

   public static <T> boolean BeNull(T reference) {
      return !BeNotNull(reference);
   }

   public static <T> boolean BeNotNull(Iterable<T> collection) {
      return !IterableUtils.isEmpty(collection);
   }

   public static <T> boolean BeNull(Iterable<T> collection) {
      return !BeNotNull(collection);
   }

   public static <T> Boolean BeNotEmpty(Iterable<T> coll) {
      return !IterableUtils.isEmpty(coll);
   }

   public static <T> Boolean BeEmpty(Iterable<T> coll) {
      return !BeNotEmpty(coll);
   }

   public static <K, V> boolean BeNotEmpty(Map<K, V> map) {
      return MapUtils.isNotEmpty(map);
   }

   public static <K, V> boolean BeEmpty(Map<K, V> map) {
      return !BeNotEmpty(map);
   }

   public static <T> Boolean BeNotEmpty(T[] arr) {
      return ArrayUtils.isNotEmpty(arr);
   }

   public static <T> Boolean BeEmpty(T[] arr) {
      return !BeNotEmpty(arr);
   }

   public static Boolean BeNotEmpty(CharSequence cs) {
      return StringUtils.isNotBlank(cs);
   }

   public static Boolean BeEmpty(CharSequence cs) {
      return !BeNotEmpty(cs);
   }

   public static Boolean BeNotBlank(CharSequence cs) {
      return StringUtils.isNotBlank(cs);
   }

   public static Boolean BeBlank(CharSequence cs) {
      return StringUtils.isBlank(cs);
   }

   public static Boolean BeGreaterThan(Number a, Number b) {
      return a != null && b != null ? a.doubleValue() - b.doubleValue() > (double)0.0F : false;
   }

   public static Boolean BeGreaterOrEqualThan(Number a, Number b) {
      return a != null && b != null ? a.doubleValue() > b.doubleValue() || a.doubleValue() == b.doubleValue() : false;
   }

   public static Boolean beEqualThan(Number a, Number b) {
      return a != null && b != null ? a.doubleValue() == b.doubleValue() : false;
   }

   public static Boolean BeNotEqual(Object a, Object b) {
      return !a.toString().equals(b.toString());
   }
}
