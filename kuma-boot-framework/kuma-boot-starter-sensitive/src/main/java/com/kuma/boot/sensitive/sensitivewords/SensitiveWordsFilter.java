package com.kuma.boot.sensitive.sensitivewords;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.Serializable;
import java.util.List;
import java.util.NavigableSet;

public class SensitiveWordsFilter implements Serializable {
   public static final char WILDCARD_STAR = '*';
   static final int DEFAULT_INITIAL_CAPACITY = 131072;
   protected static SensitiveWordsNode[] nodes = new SensitiveWordsNode[0];
   protected static SensitiveWordsNode[] nodesUpdate;

   public SensitiveWordsFilter() {
   }

   public static String filter(String sentence) {
      return filter(sentence, '*');
   }

   public static String filter(String sentence, char replace) {
      StringPointer sp = new StringPointer(sentence + "  ");
      boolean replaced = false;

      int step;
      label45:
      for(int i = 0; i < sp.length - 2; i += step) {
         step = 1;
         int hash = sp.nextTwoCharHash(i);
         if (nodes.length == 0) {
            return sentence;
         }

         SensitiveWordsNode node = nodes[hash & nodes.length - 1];
         if (node != null) {
            for(int mix = sp.nextTwoCharMix(i); node != null; node = node.next) {
               if (node.headTwoCharMix == mix) {
                  NavigableSet<StringPointer> desSet = node.words.headSet(sp.substring(i), true);

                  for(StringPointer word : desSet.descendingSet()) {
                     if (sp.nextStartsWith(i, word)) {
                        sp.fill(i, i + word.length, replace);
                        step = word.length;
                        replaced = true;
                        continue label45;
                     }
                  }
               }
            }
         }
      }

      if (replaced) {
         String res = sp.toString();
         return res.substring(0, res.length() - 2);
      } else {
         return sentence;
      }
   }

   public static void init(List<String> words) {
      LogUtils.info("\u5f00\u59cb\u521d\u59cb\u5316\u654f\u611f\u8bcd", new Object[0]);
      nodesUpdate = new SensitiveWordsNode[131072];

      for(String word : words) {
         put(word);
      }

      nodes = nodesUpdate;
   }

   public static boolean put(String word) {
      if (word != null && word.trim().length() >= 2) {
         if (word.length() == 2 && word.matches("\\w\\w")) {
            return false;
         } else {
            StringPointer sp = new StringPointer(word.trim());
            int hash = sp.nextTwoCharHash(0);
            int mix = sp.nextTwoCharMix(0);
            int index = hash & nodesUpdate.length - 1;
            SensitiveWordsNode node = nodesUpdate[index];
            if (node == null) {
               node = new SensitiveWordsNode(mix);
               node.words.add(sp);
               nodesUpdate[index] = node;
               return true;
            } else {
               while(node.headTwoCharMix != mix) {
                  if (node.next == null) {
                     (new SensitiveWordsNode(mix, node)).words.add(sp);
                     return true;
                  }

                  node = node.next;
               }

               node.words.add(sp);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static void remove(String word) {
      StringPointer sp = new StringPointer(word.trim());
      int hash = sp.nextTwoCharHash(0);
      int mix = sp.nextTwoCharMix(0);
      int index = hash & nodes.length - 1;

      for(SensitiveWordsNode node = nodes[index]; node != null; node = node.next) {
         if (node.headTwoCharMix == mix) {
            node.words.remove(sp);
         }
      }

   }
}
