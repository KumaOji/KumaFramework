package com.kuma.boot.data.jpa.fenix.core;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.bean.SqlInfo;
import com.kuma.boot.data.jpa.fenix.config.FenixConfigManager;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.exception.NodeNotFoundException;
import com.kuma.boot.data.jpa.fenix.helper.ParseHelper;
import com.kuma.boot.data.jpa.fenix.helper.SqlInfoPrinter;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.helper.XmlNodeHelper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Node;

public final class FenixXmlBuilder {
   private static final Pattern PATTERN = Pattern.compile("(#\\{[^}]+})");
   private static final FenixConfigManager fenixConfigManager = FenixConfigManager.getInstance();

   public FenixXmlBuilder() {
   }

   static SqlInfo getXmlSqlInfo(String fullFenixId, Object context) {
      if (!fullFenixId.contains(".")) {
         throw new FenixException("\u3010Fenix \u5f02\u5e38\u3011fullFenixId \u53c2\u6570\u7684\u503c\u5fc5\u987b\u662f XML \u6587\u4ef6\u4e2d\u7684 namespace + '.' + fenixId \u8282\u70b9\u7684\u503c\uff0c\u5982:\u3010student.queryStudentById\u3011\u3002\u5176\u4e2d student \u4e3a namespace, queryStudentById \u4e3a XML \u6587\u4ef6\u4e2d fenixId\u3002");
      } else {
         int dotIndex = fullFenixId.lastIndexOf(".");
         return getXmlSqlInfo(fullFenixId.substring(0, dotIndex), fullFenixId.substring(dotIndex + 1), context);
      }
   }

   static SqlInfo getXmlSqlInfo(String namespace, String fenixId, Object context) {
      if (!StringHelper.isBlank(namespace) && !StringHelper.isBlank(fenixId)) {
         Node fenixNode = XmlNodeHelper.getNodeBySpaceAndId(namespace, fenixId);
         if (fenixNode == null) {
            throw new NodeNotFoundException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u672a\u627e\u5230 namespace \u4e3a:\u3010{}\u3011, fenixId \u4e3a:\u3010{}\u3011\u7684 XML \u8282\u70b9!", namespace, fenixId));
         } else {
            SqlInfo sqlInfo = buildNewSqlInfo(namespace, fenixNode, context);
            if (fenixConfigManager.getFenixConfig().isPrintSqlInfo()) {
               (new SqlInfoPrinter()).print(sqlInfo, namespace, fenixId);
            }

            return sqlInfo;
         }
      } else {
         throw new FenixException("\u3010Fenix \u5f02\u5e38\u3011\u8bf7\u8f93\u5165\u6709\u6548\u7684 namespace \u6216\u8005 fenixId \u7684\u503c\uff0c\u4e24\u8005\u4e4b\u95f4\u7528\u70b9\u53f7('.')\u9694\u5f00!");
      }
   }

   private static SqlInfo buildNewSqlInfo(String namespace, Node node, Object context) {
      SqlInfo sqlInfo = new SqlInfo();
      sqlInfo.setResultType(XmlNodeHelper.getNodeAttrText(node, "attribute::resultType"));
      return buildSqlInfo(namespace, sqlInfo, node, context);
   }

   public static SqlInfo buildSqlInfo(String namespace, SqlInfo sqlInfo, Node node, Object context) {
      for(Node n : node.selectNodes("child::node()")) {
         String nodeTypeName = n.getNodeTypeName();
         if ("Text".equals(nodeTypeName)) {
            FenixContext.buildPlainTextSqlInfo(sqlInfo, n.getText());
         } else if ("Element".equals(nodeTypeName)) {
            FenixContext.buildSqlInfo(new BuildSource(namespace, sqlInfo, n, context), n.getName());
         }
      }

      renderSqlAndOtherParams(sqlInfo, context);
      String removeText = XmlNodeHelper.getNodeAttrText(node, "attribute::removeIfExist");
      return StringHelper.isNotBlank(removeText) ? sqlInfo.removeIfExist(removeText) : sqlInfo;
   }

   private static void renderSqlAndOtherParams(SqlInfo sqlInfo, Object context) {
      String sql = StringHelper.replaceBlank(ParseHelper.parseTemplate(sqlInfo.getJoin().toString(), context));

      String hashTagText;
      String namedText;
      for(Matcher matcher = PATTERN.matcher(sql); matcher.find(); sql = sql.replace(hashTagText, ":" + namedText)) {
         hashTagText = matcher.group(1);
         String text = hashTagText.substring(2, hashTagText.length() - 1);
         namedText = StringHelper.fixDot(text);
         sqlInfo.getParams().put(namedText, ParseHelper.parseExpressWithException(text, context));
      }

      sqlInfo.setSql(StringHelper.replaceWhereAndOr(sql));
   }
}
