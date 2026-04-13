package com.kuma.boot.data.jpa.fenix.core.concrete;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.core.FenixHandler;
import com.kuma.boot.data.jpa.fenix.core.builder.XmlSqlInfoBuilder;
import com.kuma.boot.data.jpa.fenix.helper.ParseHelper;
import com.kuma.boot.data.jpa.fenix.helper.XmlNodeHelper;
import org.dom4j.Node;

public class BetweenHandler implements FenixHandler {
   public BetweenHandler() {
   }

   public void buildSqlInfo(BuildSource source) {
      Node node = source.getNode();
      if (ParseHelper.isMatch(XmlNodeHelper.getNodeAttrText(node, "attribute::match"), source.getContext())) {
         String[] valueTextArr = XmlNodeHelper.getRangeCheckNodeText(node);
         (new XmlSqlInfoBuilder(source)).buildBetweenSql(XmlNodeHelper.getAndCheckNodeText(node, "attribute::field"), XmlNodeHelper.getNodeAttrText(node, "attribute::startName"), valueTextArr[0], XmlNodeHelper.getNodeAttrText(node, "attribute::endName"), valueTextArr[1]);
      }

   }
}
