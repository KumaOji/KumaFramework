package com.kuma.boot.sensitive.sensitiveword.support.data;

import com.kuma.boot.common.utils.common.StringUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordContainsTypeEnum;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WordDataTree extends AbstractWordData {
   private WordDataTreeNode root;

   public WordDataTree() {
   }

   public synchronized void initWordData(Collection<String> collection) {
      WordDataTreeNode newRoot = new WordDataTreeNode();

      for(String word : collection) {
         if (!StringUtils.isEmpty(word)) {
            this.addWord(newRoot, word);
         }
      }

      this.root = newRoot;
   }

   protected WordContainsTypeEnum doContains(StringBuilder stringBuilder, InnerSensitiveWordContext innerContext) {
      WordDataTreeNode nowNode = this.root;
      int len = stringBuilder.length();

      for(int i = 0; i < len; ++i) {
         nowNode = this.getNowMap(nowNode, i, stringBuilder, innerContext);
         if (ObjectUtils.isNull(nowNode)) {
            return WordContainsTypeEnum.NOT_FOUND;
         }
      }

      if (nowNode.end()) {
         return WordContainsTypeEnum.CONTAINS_END;
      } else {
         return WordContainsTypeEnum.CONTAINS_PREFIX;
      }
   }

   protected void doInitWordData(Collection<String> collection) {
      WordDataTreeNode newRoot = new WordDataTreeNode();

      for(String word : collection) {
         if (!StringUtils.isEmpty(word)) {
            this.addWord(newRoot, word);
         }
      }

      this.root = newRoot;
   }

   public synchronized void doAddWord(Collection<String> collection) {
      for(String word : collection) {
         if (!StringUtils.isEmpty(word)) {
            this.addWord(this.root, word);
         }
      }

   }

   protected synchronized void doRemoveWord(Collection<String> collection) {
      for(String word : collection) {
         if (!StringUtils.isEmpty(word)) {
            this.removeWord(this.root, word);
         }
      }

   }

   private WordDataTreeNode getNowMap(WordDataTreeNode nowNode, final int index, final StringBuilder stringBuilder, final InnerSensitiveWordContext sensitiveContext) {
      IWordContext context = sensitiveContext.wordContext();
      char mappingChar = stringBuilder.charAt(index);
      WordDataTreeNode currentMap = nowNode.getSubNode(mappingChar);
      if (context.ignoreRepeat() && index > 0) {
         char preMappingChar = stringBuilder.charAt(index - 1);
         if (preMappingChar == mappingChar) {
            currentMap = nowNode;
         }
      }

      return currentMap;
   }

   public void destroy() {
      if (this.root != null) {
         this.root.destroy();
      }

   }

   private void addWord(WordDataTreeNode newRoot, String word) {
      WordDataTreeNode tempNode = newRoot;
      char[] chars = word.toCharArray();

      for(char c : chars) {
         WordDataTreeNode subNode = tempNode.getSubNode(c);
         if (subNode == null) {
            subNode = new WordDataTreeNode();
            tempNode.addSubNode(c, subNode);
         }

         tempNode = subNode;
      }

      tempNode.end(true);
   }

   private void removeWord(WordDataTreeNode root, String word) {
      WordDataTreeNode tempNode = root;
      Map<Character, WordDataTreeNode> map = new HashMap();
      char[] chars = word.toCharArray();
      int length = chars.length;

      for(int i = 0; i < length; ++i) {
         WordDataTreeNode subNode = tempNode.getSubNode(chars[i]);
         if (subNode == null) {
            return;
         }

         if (i == length - 1) {
            if (!subNode.end()) {
               return;
            }

            if (subNode.getNodeSize() > 0) {
               subNode.end(false);
               return;
            }
         }

         if (subNode.end()) {
            map.clear();
         }

         map.put(chars[i], tempNode);
         tempNode = subNode;
      }

      for(Map.Entry<Character, WordDataTreeNode> entry : map.entrySet()) {
         WordDataTreeNode value = (WordDataTreeNode)entry.getValue();
         if (value.getNodeSize() == 1) {
            value.clearNode();
            return;
         }

         value.removeNode((Character)entry.getKey());
      }

   }
}
