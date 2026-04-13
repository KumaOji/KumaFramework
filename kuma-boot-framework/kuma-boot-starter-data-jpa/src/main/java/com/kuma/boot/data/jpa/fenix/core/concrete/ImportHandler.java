package com.kuma.boot.data.jpa.fenix.core.concrete;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.core.FenixHandler;
import com.kuma.boot.data.jpa.fenix.core.FenixXmlBuilder;
import com.kuma.boot.data.jpa.fenix.exception.NodeNotFoundException;
import com.kuma.boot.data.jpa.fenix.helper.ParseHelper;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.helper.XmlNodeHelper;
import org.dom4j.Node;

public class ImportHandler implements FenixHandler {
   public ImportHandler() {
   }

   public void buildSqlInfo(BuildSource source) {
      if (!ParseHelper.isNotMatch(XmlNodeHelper.getNodeAttrText(source.getNode(), "attribute::match"), source.getContext())) {
         String nameSpaceText = XmlNodeHelper.getNodeAttrText(source.getNode(), "attribute::namespace");
         String nameSpace = StringHelper.isNotBlank(nameSpaceText) ? nameSpaceText : source.getNamespace();
         String fenixId = XmlNodeHelper.getNodeAttrText(source.getNode(), "attribute::fenixId");
         if (StringHelper.isBlank(fenixId)) {
            throw new NodeNotFoundException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011<import /> \u6807\u7b7e\u4e2d\u5b58\u5728 fenixId \u4e3a\u7a7a\u7684\u60c5\u51b5\uff0c\u8bf7\u68c0\u67e5\uff01");
         } else {
            Node node = XmlNodeHelper.getNodeBySpaceAndId(nameSpace, fenixId);
            if (node == null) {
               throw new NodeNotFoundException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011\u4ece <import /> \u6807\u7b7e\u4e2d\uff0c\u672a\u627e\u5230 namespace \u4e3a\u3010" + nameSpace + "\u3011\uff0cfenixId \u4e3a\u3010" + fenixId + "\u3011\u7684 XML \u8282\u70b9\uff0c\u8bf7\u68c0\u67e5\uff01");
            } else {
               String valueText = XmlNodeHelper.getNodeAttrText(source.getNode(), "attribute::value");
               FenixXmlBuilder.buildSqlInfo(nameSpace, source.getSqlInfo(), node, StringHelper.isNotBlank(valueText) ? ParseHelper.parseExpressWithException(valueText, source.getContext()) : source.getContext());
            }
         }
      }
   }
}
