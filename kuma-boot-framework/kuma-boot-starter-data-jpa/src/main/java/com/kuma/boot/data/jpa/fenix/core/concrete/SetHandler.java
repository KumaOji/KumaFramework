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
         throw new FenixException("【Fenix 异常提示】namespace 为【" + namespace + "】的 XML 中，<set /> 标签中第【" + i + "】个 field 属性内容是空的，请检查！");
      } else {
         return fieldText;
      }
   }
}
