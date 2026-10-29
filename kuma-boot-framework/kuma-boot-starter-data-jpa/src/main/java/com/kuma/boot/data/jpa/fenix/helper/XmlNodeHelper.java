package com.kuma.boot.data.jpa.fenix.helper;

import com.kuma.boot.data.jpa.fenix.config.FenixConfig;
import com.kuma.boot.data.jpa.fenix.config.FenixConfigManager;
import com.kuma.boot.data.jpa.fenix.exception.ConfigNotFoundException;
import com.kuma.boot.data.jpa.fenix.exception.FieldEmptyException;
import com.kuma.boot.data.jpa.fenix.exception.NodeNotFoundException;
import com.kuma.boot.data.jpa.fenix.exception.XmlParseException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Set;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.UrlResource;
import org.springframework.util.CollectionUtils;

public final class XmlNodeHelper {
   private static final FenixConfigManager fenixConfigManager = FenixConfigManager.getInstance();

   public XmlNodeHelper() {
   }

   public static Node getNodeBySpaceAndId(String namespace, String fenixId) {
      if (!fenixConfigManager.getFenixConfig().isDebug()) {
         return (Node)FenixConfig.getFenixs().get(StringHelper.concat(namespace, ".", fenixId));
      } else {
         Set<URL> urlSet = (Set)FenixConfig.getXmlUrlMap().get(namespace);
         if (CollectionUtils.isEmpty(urlSet)) {
            throw new ConfigNotFoundException("【Fenix 异常提示】在 debug 模式下，未找到命名空间为【" + namespace + "】的 XML 文件，请检查！");
         } else {
            Node node = null;

            for(URL url : urlSet) {
               Document doc;
               try {
                  InputStream in = (new UrlResource(url)).getInputStream();

                  try {
                     doc = (new SAXReader()).read(in);
                  } catch (Throwable var11) {
                     if (in != null) {
                        try {
                           in.close();
                        } catch (Throwable var10) {
                           var11.addSuppressed(var10);
                        }
                     }

                     throw var11;
                  }

                  if (in != null) {
                     in.close();
                  }
               } catch (Exception e) {
                  throw new XmlParseException("【Fenix 异常提示】读取或解析 XML 文件失败，读取到的 XML 路径是:【" + url.getPath() + "】.", e);
               }

               try {
                  node = doc.selectSingleNode("/fenixs/fenix[@id='" + fenixId + "']");
                  if (Objects.nonNull(node)) {
                     break;
                  }
               } catch (Exception var13) {
                  String var10002 = url.getPath();
                  throw new NodeNotFoundException("【Fenix 异常提示】在 XML 文件【" + var10002 + "】中未找到 ID 为【" + fenixId + "】的 Fenix 节点.");
               }
            }

            return node;
         }
      }
   }

   public static String getNodeText(Node node) {
      return node == null ? "" : node.getText();
   }

   public static String getNodeAttrText(Node node, String attrName) {
      return getNodeText(node.selectSingleNode(attrName));
   }

   public static String getAndCheckNodeText(Node node, String nodeName) {
      String text = getNodeText(node.selectSingleNode(nodeName));
      if (StringHelper.isBlank(text)) {
         throw new FieldEmptyException("【Fenix 异常】【" + node.getName() + "】节点中填写的属性不存在或者属性内容是空的！");
      } else {
         return text;
      }
   }

   public static String[] getRangeCheckNodeText(Node node) {
      String startText = getNodeText(node.selectSingleNode("attribute::start"));
      String endText = getNodeText(node.selectSingleNode("attribute::end"));
      if (StringHelper.isBlank(startText) && StringHelper.isBlank(endText)) {
         throw new FieldEmptyException("【Fenix 异常】【" + node.getName() + "】标签中填写的【start】和【end】字段值都是空的！");
      } else {
         return new String[]{startText, endText};
      }
   }
}
