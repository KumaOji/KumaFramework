package com.kuma.boot.data.mongodb.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

public class MongoJsonUtils {
   public MongoJsonUtils() {
   }

   public static List<JsonNodeInfo> getLeafNodes(JsonNode jsonNode) {
      if (jsonNode != null && jsonNode.isObject()) {
         List<JsonNodeInfo> list = new ArrayList();
         LinkedList<String> deque = new LinkedList();
         getLeafNodes(jsonNode, (JsonNode)null, deque, list);
         return list;
      } else {
         return Collections.emptyList();
      }
   }

   private static void getLeafNodes(JsonNode jsonNode, JsonNode parentNode, LinkedList<String> deque, List<JsonNodeInfo> list) {
      Iterator<Map.Entry<String, JsonNode>> iterator;
      if (parentNode == null) {
         iterator = (Iterator)jsonNode.properties();
      } else {
         iterator = (Iterator)parentNode.properties();
      }

      while(iterator.hasNext()) {
         Map.Entry<String, JsonNode> entry = (Map.Entry)iterator.next();
         String fieldName = (String)entry.getKey();
         JsonNode nextNode = (JsonNode)entry.getValue();
         if (nextNode.isObject()) {
            deque.addLast(fieldName);
            getLeafNodes(parentNode, nextNode, deque, list);
         }

         if (nextNode.isValueNode()) {
            LinkedList<String> elements = new LinkedList(deque);
            list.add(new JsonNodeInfo(elements, parentNode));
            break;
         }

         if (!deque.isEmpty()) {
            deque.removeLast();
         }
      }

   }

   public static ObjectNode buildNode(ObjectNode jsonNode, List<String> elements) {
      ObjectNode newNode = jsonNode;

      for(String element : elements) {
         if (newNode.has(element)) {
            newNode = (ObjectNode)newNode.get(element);
         } else {
            newNode = newNode.putObject(element);
         }
      }

      return newNode;
   }

   public static Map<String, Object> getAllUpdate(String prefix, String nodeKeys, ObjectNode objectNode) {
      Map<String, Object> values = new HashMap(8);
      Iterator<String> iterator = (Iterator)objectNode.propertyNames();

      while(iterator.hasNext()) {
         String fieldName = (String)iterator.next();
         JsonNode valueNode = objectNode.get(fieldName);
         if (valueNode.isValueNode()) {
            Object value;
            if (valueNode.isShort()) {
               value = valueNode.shortValue();
            } else if (valueNode.isInt()) {
               value = valueNode.intValue();
            } else if (valueNode.isLong()) {
               value = valueNode.longValue();
            } else if (valueNode.isBoolean()) {
               value = valueNode.booleanValue();
            } else if (valueNode.isFloat()) {
               value = valueNode.floatValue();
            } else if (valueNode.isDouble()) {
               value = valueNode.doubleValue();
            } else if (valueNode.isMissingNode()) {
               value = null;
            } else {
               value = valueNode.textValue();
            }

            if (value != null) {
               String valueKey = prefix + "." + nodeKeys + "." + fieldName;
               values.put(valueKey, value);
            }
         }
      }

      return values;
   }
}
