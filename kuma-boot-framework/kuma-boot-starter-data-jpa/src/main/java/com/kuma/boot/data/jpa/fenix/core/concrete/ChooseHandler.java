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
            LogUtils.debug("【Fenix 提示】<choose /> 标签中第【" + i + "】个 when 属性不存在或者内容为空，将直接进入 else 的分支条件.", new Object[0]);
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
         throw new FenixException("【Fenix 异常提示】namespace 为【" + var10002 + "】的 XML 中，<choose /> 标签中第【" + i + "】个 when 属性为 true，但是 then 属性却是空的或未填写内容，请检查！");
      } else {
         join.append(ParseHelper.parseTemplate(thenText, context));
      }
   }
}
