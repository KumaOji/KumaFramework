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
               throw new FenixException("【Fenix 异常提示】<text></text> 标签中包含了【" + n.getName() + "】的 XML 标签，只能是文本元素，请检查！");
            }

            FenixContext.buildPlainTextSqlInfo(sqlInfo, n.getText());
         }

         (new XmlSqlInfoBuilder(source)).buildTextSqlParams(XmlNodeHelper.getNodeAttrText(source.getNode(), "attribute::value"));
      }

   }
}
