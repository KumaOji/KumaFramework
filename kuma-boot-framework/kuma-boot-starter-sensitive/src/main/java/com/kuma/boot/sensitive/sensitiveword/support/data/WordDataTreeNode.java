package com.kuma.boot.sensitive.sensitiveword.support.data;

import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWordDestroy;
import java.util.HashMap;
import java.util.Map;

public class WordDataTreeNode implements ISensitiveWordDestroy {
   private boolean end;
   private Map<Character, WordDataTreeNode> subNodeMap;

   public WordDataTreeNode() {
   }

   public boolean end() {
      return this.end;
   }

   public WordDataTreeNode end(boolean end) {
      this.end = end;
      return this;
   }

   public WordDataTreeNode getSubNode(final char c) {
      return this.subNodeMap == null ? null : (WordDataTreeNode)this.subNodeMap.get(c);
   }

   public int getNodeSize() {
      return this.subNodeMap == null ? 0 : this.subNodeMap.size();
   }

   public void clearNode() {
      if (this.subNodeMap != null) {
         this.subNodeMap = null;
      }
   }

   public void removeNode(final char c) {
      if (this.subNodeMap != null) {
         this.subNodeMap.remove(c);
      }
   }

   public WordDataTreeNode addSubNode(char c, WordDataTreeNode subNode) {
      if (this.subNodeMap == null) {
         this.subNodeMap = new HashMap();
      }

      this.subNodeMap.put(c, subNode);
      return this;
   }

   public void destroy() {
      if (this.subNodeMap != null) {
         this.subNodeMap.clear();
      }

   }
}
