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
         throw new FenixException("【Fenix 异常】fullFenixId 参数的值必须是 XML 文件中的 namespace + '.' + fenixId 节点的值，如:【student.queryStudentById】。其中 student 为 namespace, queryStudentById 为 XML 文件中 fenixId。");
      } else {
         int dotIndex = fullFenixId.lastIndexOf(".");
         return getXmlSqlInfo(fullFenixId.substring(0, dotIndex), fullFenixId.substring(dotIndex + 1), context);
      }
   }

   static SqlInfo getXmlSqlInfo(String namespace, String fenixId, Object context) {
      if (!StringHelper.isBlank(namespace) && !StringHelper.isBlank(fenixId)) {
         Node fenixNode = XmlNodeHelper.getNodeBySpaceAndId(namespace, fenixId);
         if (fenixNode == null) {
            throw new NodeNotFoundException(StringHelper.format("【Fenix 异常】未找到 namespace 为:【{}】, fenixId 为:【{}】的 XML 节点!", namespace, fenixId));
         } else {
            SqlInfo sqlInfo = buildNewSqlInfo(namespace, fenixNode, context);
            if (fenixConfigManager.getFenixConfig().isPrintSqlInfo()) {
               (new SqlInfoPrinter()).print(sqlInfo, namespace, fenixId);
            }

            return sqlInfo;
         }
      } else {
         throw new FenixException("【Fenix 异常】请输入有效的 namespace 或者 fenixId 的值，两者之间用点号('.')隔开!");
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
