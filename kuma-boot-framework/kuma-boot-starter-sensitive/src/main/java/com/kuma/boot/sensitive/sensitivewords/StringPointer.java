package com.kuma.boot.sensitive.sensitivewords;

import java.io.Serializable;

public class StringPointer implements Serializable, CharSequence, Comparable<StringPointer> {
   private static final long serialVersionUID = 1L;
   protected final char[] value;
   protected final int offset;
   protected final int length;
   private int hash = 0;

   public StringPointer(String str) {
      this.value = str.toCharArray();
      this.offset = 0;
      this.length = this.value.length;
   }

   public StringPointer(char[] value, int offset, int length) {
      this.value = value;
      this.offset = offset;
      this.length = length;
   }

   public int nextTwoCharHash(int i) {
      return 31 * this.value[this.offset + i] + this.value[this.offset + i + 1];
   }

   public int nextTwoCharMix(int i) {
      return this.value[this.offset + i] << 16 | this.value[this.offset + i + 1];
   }

   public boolean nextStartsWith(int i, StringPointer word) {
      if (word.length > this.length - i) {
         return false;
      } else {
         for(int c = word.length - 1; c >= 0; --c) {
            if (this.value[this.offset + i + c] != word.value[word.offset + c]) {
               return false;
            }
         }

         return true;
      }
   }

   public void fill(int begin, int end, char fillWith) {
      for(int i = begin; i < end; ++i) {
         this.value[this.offset + i] = fillWith;
      }

   }

   public int length() {
      return this.length;
   }

   public char charAt(int i) {
      return this.value[this.offset + i];
   }

   public StringPointer substring(int begin) {
      return new StringPointer(this.value, this.offset + begin, this.length - begin);
   }

   public StringPointer substring(int begin, int end) {
      return new StringPointer(this.value, this.offset + begin, end - begin);
   }

   public CharSequence subSequence(int start, int end) {
      return this.substring(start, end);
   }

   public String toString() {
      return new String(this.value, this.offset, this.length);
   }

   public int hashCode() {
      int h = this.hash;
      if (h == 0 && this.length > 0) {
         for(int i = 0; i < this.length; ++i) {
            h = 31 * h + this.value[this.offset + i];
         }

         this.hash = h;
      }

      return h;
   }

   public boolean equals(Object anObject) {
      if (this == anObject) {
         return true;
      } else {
         if (anObject instanceof StringPointer) {
            StringPointer that = (StringPointer)anObject;
            if (this.length == that.length) {
               for(int i = 0; i < this.length; ++i) {
                  if (this.value[this.offset + i] != that.value[that.offset + i]) {
                     return false;
                  }
               }

               return true;
            }
         }

         return false;
      }
   }

   public int compareTo(StringPointer that) {
      int len1 = this.length;
      int len2 = that.length;
      int lim = Math.min(len1, len2);

      for(int k = 0; k < lim; ++k) {
         char c1 = this.value[this.offset + k];
         char c2 = that.value[that.offset + k];
         if (c1 != c2) {
            return c1 - c2;
         }
      }

      return len1 - len2;
   }
}
