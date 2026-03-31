package com.kuma.boot.monitor.strategy;

import com.kuma.boot.common.model.PropertyCache;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.convert.ConvertUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.model.Report;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.util.StringUtils;

public class Rule {
   public Rule() {
   }

   public static class RuleInfo implements Serializable {
      private RuleType type;
      private Object value;
      private HitCallBack hitCallBack;

      public RuleInfo() {
      }

      public RuleInfo(RuleType type, Object value, HitCallBack hitCallBack) {
         this.type = type;
         this.value = value;
         this.hitCallBack = hitCallBack;
      }

      public boolean check(Object checkValue) {
         if (checkValue == null) {
            return false;
         } else {
            try {
               if (checkValue instanceof Number) {
                  double checkValue2 = ((Number)checkValue).doubleValue();
                  double warnValue = ((Number)ConvertUtils.convert(this.value, Number.class)).doubleValue();
                  if (this.type == Rule.RuleType.less && checkValue2 < warnValue) {
                     return true;
                  }

                  if (this.type == Rule.RuleType.more && checkValue2 > warnValue) {
                     return true;
                  }

                  if (this.type == Rule.RuleType.equal && checkValue2 == warnValue) {
                     return true;
                  }
               } else {
                  String checkValue2 = checkValue.toString();
                  String warnValue = this.value.toString();
                  if (this.type == Rule.RuleType.equal && Objects.equals(checkValue2, warnValue)) {
                     return true;
                  }

                  if (this.type == Rule.RuleType.contain && checkValue2.contains(warnValue)) {
                     return true;
                  }
               }
            } catch (Exception exp) {
               LogUtils.error("health", new Object[]{"check \u89c4\u5219\u68c0\u67e5\u51fa\u9519", exp});
            }

            return false;
         }
      }

      public RuleType getType() {
         return this.type;
      }

      public void setType(RuleType type) {
         this.type = type;
      }

      public Object getValue() {
         return this.value;
      }

      public void setValue(Object value) {
         this.value = value;
      }

      public HitCallBack getHitCallBack() {
         return this.hitCallBack;
      }

      public void setHitCallBack(HitCallBack hitCallBack) {
         this.hitCallBack = hitCallBack;
      }
   }

   public static class RulesAnalyzer {
      private final Map<String, List<RuleInfo>> rules = new HashMap();
      private final RuleParser ruleParser = new RuleParser();

      public RulesAnalyzer() {
         PropertyCache propertyCache = (PropertyCache)ContextUtils.getBean(PropertyCache.class, false);
         if (Objects.nonNull(propertyCache)) {
            propertyCache.listenUpdateCache("RulesAnalyzer \u52a8\u6001\u89c4\u5219\u8ba2\u9605", (map) -> {
               for(Map.Entry<String, Object> e : map.entrySet()) {
                  String key = (String)e.getKey();
                  if (StringUtils.startsWithIgnoreCase(key, "kmc.monitor.strategy.")) {
                     key = key.replace("kmc.monitor.strategy.", "");
                     Object rule = this.rules.get(key);
                     if (rule != null) {
                        this.registerRules(key, com.kuma.boot.common.utils.lang.StringUtils.nullToEmpty(e.getValue()));
                     }
                  }
               }

            });
         }

      }

      public List<RuleInfo> parserRules(String rules) {
         return this.ruleParser.parser(rules);
      }

      public List<RuleInfo> getRules(String field) {
         List<RuleInfo> item = (List)this.rules.get(field);
         if (item == null) {
            this.registerRulesByProperties(field);
         }

         return (List)this.rules.get(field);
      }

      public void registerRules(String field, List<RuleInfo> rules) {
         this.rules.put(field, rules);
      }

      public void registerRules(String field, String rules) {
         this.registerRules(field, this.ruleParser.parser(rules));
      }

      public void registerRulesByProperties(String field) {
         String value = (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy." + field, "");
         this.registerRules(field, value);
      }

      public Report analyse(Report report) {
         report.eachReport((fieldName, item) -> {
            List<RuleInfo> rules = this.getRules(fieldName);
            if (rules != null && !rules.isEmpty()) {
               for(RuleInfo ruleInfo : rules) {
                  boolean isWarn = ruleInfo.check(item.getValue());
                  if (isWarn) {
                     item.setWarn("\u62a5\u8b66");
                     item.setRule(ruleInfo);
                     if (ruleInfo.getHitCallBack() != null) {
                        try {
                           ruleInfo.hitCallBack.run(item.getValue());
                        } catch (Exception exp) {
                           LogUtils.error("health", new Object[]{"analyse\u5206\u6790\u65f6\u6267\u884c\u62a5\u8b66\u56de\u8c03\u89c4\u5219\u51fa\u9519", exp});
                        }
                     }
                  }
               }
            }

            return item;
         });
         return report;
      }
   }

   public static enum RuleType {
      more(">", "\u5927\u4e8e"),
      less("<", "\u5c0f\u4e8e"),
      equal("=", "\u7b49\u4e8e"),
      contain("%", "\u5305\u542b");

      private final String desc;
      private final String tag;

      private RuleType(String tag, String desc) {
         this.desc = desc;
         this.tag = tag;
      }

      public static RuleType getRuleType(String tag) {
         for(RuleType type : values()) {
            if (type.tag.equalsIgnoreCase(tag)) {
               return type;
            }
         }

         return null;
      }

      // $FF: synthetic method
      private static RuleType[] $values() {
         return new RuleType[]{more, less, equal, contain};
      }
   }

   public static class RuleParser {
      public RuleParser() {
      }

      public List<RuleInfo> parser(String text) {
         List<RuleInfo> result = new ArrayList();

         try {
            if (com.kuma.boot.common.utils.lang.StringUtils.isNotBlank(text) && text.startsWith("[") && text.endsWith("]")) {
               text = text.replace("[", "").replace("]", "");
               String[] rules = text.split(";");

               for(String r : rules) {
                  RuleType type = Rule.RuleType.getRuleType("" + r.charAt(0));
                  String value = StringUtils.trimLeadingCharacter(r, r.charAt(0));
                  if (type != null) {
                     result.add(new RuleInfo(type, value, (HitCallBack)null));
                  }
               }
            }
         } catch (Exception exp) {
            LogUtils.error("health", new Object[]{"parser\u89c4\u5219\u89e3\u6790\u51fa\u9519", exp});
         }

         return result;
      }
   }

   public interface HitCallBack {
      void run(Object value);
   }
}
