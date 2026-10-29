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
            throw new NodeNotFoundException("【Fenix 异常提示】<import /> 标签中存在 fenixId 为空的情况，请检查！");
         } else {
            Node node = XmlNodeHelper.getNodeBySpaceAndId(nameSpace, fenixId);
            if (node == null) {
               throw new NodeNotFoundException("【Fenix 异常提示】从 <import /> 标签中，未找到 namespace 为【" + nameSpace + "】，fenixId 为【" + fenixId + "】的 XML 节点，请检查！");
            } else {
               String valueText = XmlNodeHelper.getNodeAttrText(source.getNode(), "attribute::value");
               FenixXmlBuilder.buildSqlInfo(nameSpace, source.getSqlInfo(), node, StringHelper.isNotBlank(valueText) ? ParseHelper.parseExpressWithException(valueText, source.getContext()) : source.getContext());
            }
         }
      }
   }
}
