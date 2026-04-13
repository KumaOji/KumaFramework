package com.kuma.boot.data.jpa.fenix.config;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.fenix.config.scanner.TaggerScanner;
import com.kuma.boot.data.jpa.fenix.config.scanner.XmlResource;
import com.kuma.boot.data.jpa.fenix.config.scanner.XmlScanner;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.exception.NodeNotFoundException;
import com.kuma.boot.data.jpa.fenix.helper.ParamWrapper;
import com.kuma.boot.data.jpa.fenix.helper.ParseHelper;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.helper.XmlNodeHelper;
import com.kuma.boot.data.jpa.fenix.jpa.transformer.PrefixUnderscoreTransformer;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.dom4j.Node;

public final class FenixConfigManager {
   private static final String BANNER_TEXT = "\n___________           .__        \n\\_   _____/___   ____ |__|__  ___\n |    __)/ __ \\ /    \\|  \\  \\/  /\n |     \\\\  ___/|   |  \\  |>    < \n \\___  / \\___  >___|  /__/__/\\_ \\\n     \\/      \\/     \\/         \\/ v4.0.0\n";
   private FenixConfig fenixConfig;
   private static final FenixConfigManager confManager = new FenixConfigManager();

   public FenixConfigManager() {
   }

   public FenixConfig getFenixConfig() {
      return this.fenixConfig;
   }

   public static FenixConfigManager getInstance() {
      return confManager;
   }

   public void initLoad() {
      this.initLoad(new FenixConfig());
   }

   public void initLoad(FenixConfig fenixConfig) {
      this.initFenixConfig(fenixConfig);
      this.cachingFenixXmlResources((new XmlScanner()).scan(this.fenixConfig.getXmlLocations()));
      (new TaggerScanner()).scan(this.fenixConfig.getHandlerLocations());
      this.asyncTestFirstEvaluate();
      this.printBanner();
   }

   private void initFenixConfig(FenixConfig fenixConfig) {
      if (fenixConfig == null) {
         throw new FenixException("\u3010Fenix \u5f02\u5e38\u3011\u521d\u59cb\u5316\u52a0\u8f7d\u7684 FenixConfig \u914d\u7f6e\u4fe1\u606f\u5b9e\u4f8b\u4e3a\u7a7a\uff0c\u8bf7\u68c0\u67e5\uff01");
      } else {
         this.trySetUnderscoreTransformerPrefix(fenixConfig.getUnderscoreTransformerPrefix());
         String xmlLocations = fenixConfig.getXmlLocations();
         fenixConfig.setXmlLocations(StringHelper.isBlank(xmlLocations) ? "fenix" : xmlLocations);
         this.fenixConfig = fenixConfig;
      }
   }

   private void trySetUnderscoreTransformerPrefix(String underscoreTransformerPrefix) {
      if (StringHelper.isNotBlank(underscoreTransformerPrefix)) {
         Set<String> prefixSet = PrefixUnderscoreTransformer.getPrefixSet();
         prefixSet.clear();

         for(String prefix : underscoreTransformerPrefix.split(",")) {
            prefixSet.add(prefix.trim());
         }
      }

   }

   private void printBanner() {
      if (this.fenixConfig.isPrintBanner()) {
         LogUtils.warn("\n___________           .__        \n\\_   _____/___   ____ |__|__  ___\n |    __)/ __ \\ /    \\|  \\  \\/  /\n |     \\\\  ___/|   |  \\  |>    < \n \\___  / \\___  >___|  /__/__/\\_ \\\n     \\/      \\/     \\/         \\/ v4.0.0\n", new Object[0]);
      }

      if (this.fenixConfig.isDebug()) {
         LogUtils.warn("\u3010Fenix \u63d0\u793a\u3011\u5df2\u5f00\u542f Fenix \u7684\u3010debug\u3011\u6a21\u5f0f\uff0c\u4ec5\u5efa\u8bae\u4f60\u5728\u5f00\u53d1\u73af\u5883\u4e2d\u5f00\u542f\u6b64\u6a21\u5f0f.", new Object[0]);
      }

      LogUtils.warn("\u3010Fenix \u63d0\u793a\u3011\u521d\u59cb\u5316\u52a0\u8f7d Fenix \u914d\u7f6e\u4fe1\u606f\u5b8c\u6210.", new Object[0]);
   }

   private void cachingFenixXmlResources(Map<String, XmlResource> xmlResourceMap) {
      if (LogUtils.isDebugEnabled()) {
         LogUtils.debug("\u3010Fenix \u63d0\u793a\u3011\u626b\u63cf\u5230\u4e86\u8fd9\u4e9b Fenix XML \u6587\u4ef6\uff1a\u3010{}\u3011.", new Object[]{xmlResourceMap.keySet()});
      }

      boolean debug = this.fenixConfig.isDebug();
      Map<String, Set<URL>> xmlUrlMap = FenixConfig.getXmlUrlMap();

      for(XmlResource xmlResource : xmlResourceMap.values()) {
         String namespace = xmlResource.getNamespace();

         for(Node fenixNode : xmlResource.getDocument().selectNodes("fenixs/fenix")) {
            String fenixId = XmlNodeHelper.getNodeText(fenixNode.selectSingleNode("attribute::id"));
            if (StringHelper.isBlank(fenixId)) {
               throw new NodeNotFoundException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011\u547d\u540d\u7a7a\u95f4\u4e3a\u3010" + namespace + "\u3011\u7684 Fenix XML \u6587\u4ef6\u4e2d\u6709 fenix \u8282\u70b9\u7684 id \u5c5e\u6027\u4e3a\u7a7a\uff0c\u8bf7\u68c0\u67e5\uff01\u6587\u4ef6\u4f4d\u7f6e\u5728\u3010" + xmlResource.getUrl().getPath() + "\u3011.");
            }

            if (fenixId.contains(".")) {
               throw new FenixException("\u3010Fenix \u5f02\u5e38\u63d0\u793a\u3011\u547d\u540d\u7a7a\u95f4\u4e3a\u3010" + namespace + "\u3011\u7684 XML \u6587\u4ef6\u4e2d\uff0cfenix \u8282\u70b9 id\u3010" + fenixId + "\u3011\u4e0d\u80fd\u5305\u542b '.' \u53f7\uff0c\u8bf7\u4fee\u6b63\uff01\u6587\u4ef6\u4f4d\u7f6e\u5728\u3010" + xmlResource.getUrl().getPath() + "\u3011.");
            }

            FenixConfig.getFenixs().put(StringHelper.concat(namespace, ".", fenixId), fenixNode);
            if (debug) {
               Set<URL> urlSet = (Set)xmlUrlMap.computeIfAbsent(namespace, (k) -> new HashSet());
               urlSet.add(xmlResource.getUrl());
            }
         }
      }

   }

   public void clear() {
      FenixConfig.getFenixs().clear();
      FenixConfig.getTagHandlerMap().clear();
   }

   private void asyncTestFirstEvaluate() {
      CompletableFuture.runAsync(() -> {
         try {
            Map<String, Object> context = ParamWrapper.newInstance("foo", "hello").toMap();
            ParseHelper.parseTemplate("@if{?foo != empty}Hello World!@end{}", context);
            ParseHelper.parseExpressWithException("foo != empty", context);
         } catch (Exception e) {
            LogUtils.error("\u3010Fenix \u5f02\u5e38\u3011\u521d\u6b21\u6d4b\u8bd5\u6267\u884c MVEL \u8868\u8fbe\u5f0f\u65f6\u5f02\u5e38\uff01", new Object[]{e});
         }

      });
   }
}
