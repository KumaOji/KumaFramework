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
         LogUtils.error("【Fenix 错误警示】解析表达式出错，表达式为:【{}】.", new Object[]{exp, e});
         return null;
      }
   }

   public static Object parseExpressWithException(String exp, Object context) {
      try {
         return MVEL.eval(exp, context);
      } catch (Exception e) {
         throw new ParseExpressionException("【Fenix 异常提示】解析表达式异常，解析出错的表达式为:【" + exp + "】.", e);
      }
   }

   public static String parseTemplate(String template, Object context) {
      try {
         return (String)TemplateRuntime.eval(template, context);
      } catch (Exception e) {
         throw new ParseExpressionException("【Fenix 异常提示】解析模板异常，解析出错的模板为:【" + template + "】.", e);
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
