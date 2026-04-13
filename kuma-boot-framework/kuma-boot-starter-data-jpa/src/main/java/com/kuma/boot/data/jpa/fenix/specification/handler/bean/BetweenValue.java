package com.kuma.boot.data.jpa.fenix.specification.handler.bean;

public class BetweenValue<T extends Comparable<T>> {
   private final T start;
   private final T end;

   public T getStart() {
      return this.start;
   }

   public T getEnd() {
      return this.end;
   }

   public BetweenValue(T start, T end) {
      this.start = start;
      this.end = end;
   }

   public static <T extends Comparable<T>> BetweenValue<T> of(T start, T end) {
      return new BetweenValue<T>(start, end);
   }

   public static <T extends Comparable<T>> BetweenValue<T> ofStart(T start) {
      return new BetweenValue<T>(start, (Comparable)null);
   }

   public static <T extends Comparable<T>> BetweenValue<T> ofEnd(T end) {
      return new BetweenValue<T>((Comparable)null, end);
   }
}
