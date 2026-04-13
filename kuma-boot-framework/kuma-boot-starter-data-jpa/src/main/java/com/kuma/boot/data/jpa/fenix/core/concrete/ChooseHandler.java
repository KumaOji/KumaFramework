package com.kuma.boot.data.jpa.fenix.core.concrete;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.core.FenixHandler;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.ParseHelper;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.helper.XmlNodeHelper;
import org.dom4j.Node;

public class ChooseHandler implements FenixHandler {
   public ChooseHandler() {
   }

   public void buildSqlInfo(BuildSource source) {
      Object context = source.getContext();
      StringBuilder join = source.getSqlInfo().getJoin();
      Node node = source.getNode();
      int i = 0;

      String x;
      String whenText;
      do {
         ++i;
         x = i == 1 ? "" : Integer.toString(i);
         whenText = XmlNodeHelper.getNodeAttrText(node, "attribute::when" + x);
         if (StringHelper.isBlank(whenText)) {
            LogUtils.debug("\u3010Fenix \u63d0\u793a\u3011<choose /> \u6807\u7b7e\u4e2d\u7b2c\u3010" + i + "\u3011\u4e2a when \u5c5e\u6027\u4e0d\u5b58\u5728\u6216\u8005\u5185\u5bb9\u4e3a\u7a7a\uff0c\u5c06\u76f4\u63a5\u8fdb\u5165 else \u7684\u5206\u652f\u6761\u4ef6.", new Object[0]);
            x = XmlNodeHelper.getNodeAttrText(node, "attribute::else");
            if (StringHelper.isNotBlank(x)) {
               join.append(ParseHelper.parseTemplate(x, context));
            }

            return;
         }
      } while(!ParseHelper.isTrue(whenText, context));

      String thenText = XmlNodeHelper.getNodeAttrText(node, "attribute::then" + x);
      if (StringHelper.isBlank(thenText)) {
         String var10002 = source.getNamespace();
         throw new FenixException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011namespace \u4e3a\u3010" + var10002 + "\u3011\u7684 XML \u4e2d\uff0c<choose /> \u6807\u7b7e\u4e2d\u7b2c\u3010" + i + "\u3011\u4e2a when \u5c5e\u6027\u4e3a true\uff0c\u4f46\u662f then \u5c5e\u6027\u5374\u662f\u7a7a\u7684\u6216\u672a\u586b\u5199\u5185\u5bb9\uff0c\u8bf7\u68c0\u67e5\uff01");
      } else {
         join.append(ParseHelper.parseTemplate(thenText, context));
      }
   }
}
