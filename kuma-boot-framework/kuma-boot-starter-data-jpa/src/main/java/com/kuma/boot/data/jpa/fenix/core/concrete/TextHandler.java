package com.kuma.boot.data.jpa.fenix.core.concrete;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.bean.SqlInfo;
import com.kuma.boot.data.jpa.fenix.core.FenixContext;
import com.kuma.boot.data.jpa.fenix.core.FenixHandler;
import com.kuma.boot.data.jpa.fenix.core.builder.XmlSqlInfoBuilder;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.ParseHelper;
import com.kuma.boot.data.jpa.fenix.helper.XmlNodeHelper;
import org.dom4j.Node;

public class TextHandler implements FenixHandler {
   public TextHandler() {
   }

   public void buildSqlInfo(BuildSource source) {
      Node node = source.getNode();
      if (ParseHelper.isMatch(XmlNodeHelper.getNodeAttrText(node, "attribute::match"), source.getContext())) {
         SqlInfo sqlInfo = source.getSqlInfo();

         for(Node n : node.selectNodes("child::node()")) {
            if (!"Text".equals(n.getNodeTypeName())) {
               throw new FenixException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011<text></text> \u6807\u7b7e\u4e2d\u5305\u542b\u4e86\u3010" + n.getName() + "\u3011\u7684 XML \u6807\u7b7e\uff0c\u53ea\u80fd\u662f\u6587\u672c\u5143\u7d20\uff0c\u8bf7\u68c0\u67e5\uff01");
            }

            FenixContext.buildPlainTextSqlInfo(sqlInfo, n.getText());
         }

         (new XmlSqlInfoBuilder(source)).buildTextSqlParams(XmlNodeHelper.getNodeAttrText(source.getNode(), "attribute::value"));
      }

   }
}
