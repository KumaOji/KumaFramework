package com.kuma.boot.data.jpa.fenix.helper;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.fenix.exception.ParseExpressionException;
import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

public final class ParseHelper {
   public ParseHelper() {
   }

   public static Object parseExpress(String exp, Object context) {
      try {
         return MVEL.eval(exp, context);
      } catch (Exception e) {
         LogUtils.error("\u3010Fenix \u9519\u8bef\u8b66\u793a\u3011\u89e3\u6790\u8868\u8fbe\u5f0f\u51fa\u9519\uff0c\u8868\u8fbe\u5f0f\u4e3a:\u3010{}\u3011.", new Object[]{exp, e});
         return null;
      }
   }

   public static Object parseExpressWithException(String exp, Object context) {
      try {
         return MVEL.eval(exp, context);
      } catch (Exception e) {
         throw new ParseExpressionException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011\u89e3\u6790\u8868\u8fbe\u5f0f\u5f02\u5e38\uff0c\u89e3\u6790\u51fa\u9519\u7684\u8868\u8fbe\u5f0f\u4e3a:\u3010" + exp + "\u3011.", e);
      }
   }

   public static String parseTemplate(String template, Object context) {
      try {
         return (String)TemplateRuntime.eval(template, context);
      } catch (Exception e) {
         throw new ParseExpressionException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011\u89e3\u6790\u6a21\u677f\u5f02\u5e38\uff0c\u89e3\u6790\u51fa\u9519\u7684\u6a21\u677f\u4e3a:\u3010" + template + "\u3011.", e);
      }
   }

   public static boolean isMatch(String match, Object context) {
      return StringHelper.isBlank(match) || isTrue(match, context);
   }

   public static boolean isNotMatch(String match, Object context) {
      return !isMatch(match, context);
   }

   public static boolean isTrue(String exp, Object context) {
      return Boolean.TRUE.equals(parseExpressWithException(exp, context));
   }
}
