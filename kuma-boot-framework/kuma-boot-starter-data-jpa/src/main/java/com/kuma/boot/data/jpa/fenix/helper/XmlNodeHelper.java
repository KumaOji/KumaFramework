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
            throw new ConfigNotFoundException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011\u5728 debug \u6a21\u5f0f\u4e0b\uff0c\u672a\u627e\u5230\u547d\u540d\u7a7a\u95f4\u4e3a\u3010" + namespace + "\u3011\u7684 XML \u6587\u4ef6\uff0c\u8bf7\u68c0\u67e5\uff01");
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
                  throw new XmlParseException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011\u8bfb\u53d6\u6216\u89e3\u6790 XML \u6587\u4ef6\u5931\u8d25\uff0c\u8bfb\u53d6\u5230\u7684 XML \u8def\u5f84\u662f:\u3010" + url.getPath() + "\u3011.", e);
               }

               try {
                  node = doc.selectSingleNode("/fenixs/fenix[@id='" + fenixId + "']");
                  if (Objects.nonNull(node)) {
                     break;
                  }
               } catch (Exception var13) {
                  String var10002 = url.getPath();
                  throw new NodeNotFoundException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011\u5728 XML \u6587\u4ef6\u3010" + var10002 + "\u3011\u4e2d\u672a\u627e\u5230 ID \u4e3a\u3010" + fenixId + "\u3011\u7684 Fenix \u8282\u70b9.");
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
         throw new FieldEmptyException("\u3010Fenix \u5f02\u5e38\u3011\u3010" + node.getName() + "\u3011\u8282\u70b9\u4e2d\u586b\u5199\u7684\u5c5e\u6027\u4e0d\u5b58\u5728\u6216\u8005\u5c5e\u6027\u5185\u5bb9\u662f\u7a7a\u7684\uff01");
      } else {
         return text;
      }
   }

   public static String[] getRangeCheckNodeText(Node node) {
      String startText = getNodeText(node.selectSingleNode("attribute::start"));
      String endText = getNodeText(node.selectSingleNode("attribute::end"));
      if (StringHelper.isBlank(startText) && StringHelper.isBlank(endText)) {
         throw new FieldEmptyException("\u3010Fenix \u5f02\u5e38\u3011\u3010" + node.getName() + "\u3011\u6807\u7b7e\u4e2d\u586b\u5199\u7684\u3010start\u3011\u548c\u3010end\u3011\u5b57\u6bb5\u503c\u90fd\u662f\u7a7a\u7684\uff01");
      } else {
         return new String[]{startText, endText};
      }
   }
}
