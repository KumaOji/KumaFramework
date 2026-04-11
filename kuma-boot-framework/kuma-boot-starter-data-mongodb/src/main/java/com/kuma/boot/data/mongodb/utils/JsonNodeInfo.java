package com.kuma.boot.data.mongodb.utils;

import java.util.LinkedList;
import java.util.Objects;
import java.util.StringJoiner;
import org.springframework.util.Assert;
import tools.jackson.databind.JsonNode;

public class JsonNodeInfo {
   private final String nodeKeys;
   private final String nodePath;
   private final LinkedList<String> elements;
   private final JsonNode leafNode;

   public JsonNodeInfo(LinkedList<String> elements, JsonNode leafNode) {
      Assert.notNull(elements, "elements can not be null.");
      this.nodeKeys = getNodeKeys(elements);
      this.nodePath = getNodePath(elements);
      this.elements = elements;
      this.leafNode = leafNode;
   }

   private static String getNodeKeys(LinkedList<String> elements) {
      StringJoiner nodeKeysJoiner = new StringJoiner(".");
      Objects.requireNonNull(nodeKeysJoiner);
      elements.forEach(nodeKeysJoiner::add);
      return nodeKeysJoiner.toString();
   }

   private static String getNodePath(LinkedList<String> elements) {
      StringJoiner nodePathJoiner = new StringJoiner("/", "/", "");
      Objects.requireNonNull(nodePathJoiner);
      elements.forEach(nodePathJoiner::add);
      return nodePathJoiner.toString();
   }

   public String getFirst() {
      return (String)this.elements.getFirst();
   }

   public String getNodeKeys() {
      return this.nodeKeys;
   }

   public String getNodePath() {
      return this.nodePath;
   }

   public LinkedList<String> getElements() {
      return this.elements;
   }

   public JsonNode getLeafNode() {
      return this.leafNode;
   }
}
