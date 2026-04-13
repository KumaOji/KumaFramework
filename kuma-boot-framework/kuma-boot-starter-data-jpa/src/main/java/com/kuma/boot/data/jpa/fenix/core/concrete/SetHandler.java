package com.kuma.boot.data.jpa.fenix.core.concrete;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.core.FenixHandler;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.ParseHelper;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.helper.XmlNodeHelper;
import java.util.Map;
import org.dom4j.Node;

public class SetHandler implements FenixHandler {
   public SetHandler() {
   }

   public void buildSqlInfo(BuildSource source) {
      Object context = source.getContext();
      String namespace = source.getNamespace();
      StringBuilder join = source.getSqlInfo().getJoin();
      Map<String, Object> params = source.getSqlInfo().getParams();
      Node node = source.getNode();
      int i = 0;
      boolean isSet = false;

      while(true) {
         ++i;
         String x = i == 1 ? "" : Integer.toString(i);
         Node fieldNode = node.selectSingleNode("attribute::field" + x);
         if (fieldNode == null) {
            return;
         }

         if (ParseHelper.isMatch(XmlNodeHelper.getNodeAttrText(node, "attribute::match" + x), context)) {
            String fieldText = this.getAndCheckFieldText(fieldNode, namespace, i);
            if (!isSet) {
               join.append(" SET ");
               isSet = true;
            } else {
               join.append(",").append(" ");
            }

            Node valueNode = node.selectSingleNode("attribute::value" + x);
            join.append(fieldText).append(" = ").append(":").append(fieldText);
            params.put(fieldText, valueNode == null ? null : ParseHelper.parseExpressWithException(valueNode.getText(), context));
         }
      }
   }

   private String getAndCheckFieldText(Node fieldNode, String namespace, int i) {
      String fieldText = fieldNode.getText();
      if (StringHelper.isBlank(fieldText)) {
         throw new FenixException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011namespace \u4e3a\u3010" + namespace + "\u3011\u7684 XML \u4e2d\uff0c<set /> \u6807\u7b7e\u4e2d\u7b2c\u3010" + i + "\u3011\u4e2a field \u5c5e\u6027\u5185\u5bb9\u662f\u7a7a\u7684\uff0c\u8bf7\u68c0\u67e5\uff01");
      } else {
         return fieldText;
      }
   }
}
