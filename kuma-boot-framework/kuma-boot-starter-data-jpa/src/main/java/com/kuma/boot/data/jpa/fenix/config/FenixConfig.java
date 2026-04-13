package com.kuma.boot.data.jpa.fenix.config;

import com.kuma.boot.data.jpa.fenix.config.entity.TagHandler;
import com.kuma.boot.data.jpa.fenix.core.FenixHandler;
import com.kuma.boot.data.jpa.fenix.core.FenixHandlerFactory;
import com.kuma.boot.data.jpa.fenix.core.concrete.BetweenHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.ChooseHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.EndsWithHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.ImportHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.InHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.IsNullHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.LikeHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.NormalHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.SetHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.StartsWithHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.TextHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.TrimWhereHandler;
import com.kuma.boot.data.jpa.fenix.core.concrete.WhereHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.BetweenPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.EndsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.EqualsPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.GreaterThanEqualPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.GreaterThanPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.InPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.IsNotNullPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.IsNullPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.JoinPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.LessThanEqualPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.LessThanPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.LikeInPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.LikeOrLikePredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.LikePatternPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.LikePredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotBetweenPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotEndsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotEqualsPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotInPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotLikePatternPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotLikePredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotStartsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrBetweenPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrEndsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrEqualsPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrGreaterThanEqualPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrGreaterThanPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrInPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrIsNotNullPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrIsNullPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrLessThanEqualPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrLessThanPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrLikeOrLikePredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrLikePatternPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrLikePredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotBetweenPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotEndsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotEqualsPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotInPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotLikePatternPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotLikePredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotStartsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrStartsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.StartsWithPredicateHandler;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.dom4j.Node;

public class FenixConfig {
   private boolean debug;
   private boolean printBanner = true;
   private boolean printSqlInfo;
   protected String xmlLocations = "fenix";
   protected String handlerLocations;
   private String underscoreTransformerPrefix;
   private static final Map<String, Node> fenixs = new HashMap();
   private static final Map<String, Set<URL>> xmlUrlMap = new HashMap();
   private static final Map<String, TagHandler> tagHandlerMap = new HashMap(128);
   private static final Map<Class<?>, AbstractPredicateHandler> specificationHandlerMap = new HashMap(64);

   public FenixConfig() {
   }

   public static Map<String, Set<URL>> getXmlUrlMap() {
      return xmlUrlMap;
   }

   public static Map<String, TagHandler> getTagHandlerMap() {
      return tagHandlerMap;
   }

   public static Map<Class<?>, AbstractPredicateHandler> getSpecificationHandlerMap() {
      return specificationHandlerMap;
   }

   public static Map<String, Node> getFenixs() {
      return fenixs;
   }

   private static void initDefaultTagHandler() {
      add("equal", NormalHandler::new, " = ");
      add("andEqual", " AND ", NormalHandler::new, " = ");
      add("orEqual", " OR ", NormalHandler::new, " = ");
      add("notEqual", NormalHandler::new, " <> ");
      add("andNotEqual", " AND ", NormalHandler::new, " <> ");
      add("orNotEqual", " OR ", NormalHandler::new, " <> ");
      add("greaterThan", NormalHandler::new, " > ");
      add("andGreaterThan", " AND ", NormalHandler::new, " > ");
      add("orGreaterThan", " OR ", NormalHandler::new, " > ");
      add("lessThan", NormalHandler::new, " < ");
      add("andLessThan", " AND ", NormalHandler::new, " < ");
      add("orLessThan", " OR ", NormalHandler::new, " < ");
      add("greaterThanEqual", NormalHandler::new, " >= ");
      add("andGreaterThanEqual", " AND ", NormalHandler::new, " >= ");
      add("orGreaterThanEqual", " OR ", NormalHandler::new, " >= ");
      add("lessThanEqual", NormalHandler::new, " <= ");
      add("andLessThanEqual", " AND ", NormalHandler::new, " <= ");
      add("orLessThanEqual", " OR ", NormalHandler::new, " <= ");
      add("like", LikeHandler::new, " LIKE ");
      add("andLike", " AND ", LikeHandler::new, " LIKE ");
      add("orLike", " OR ", LikeHandler::new, " LIKE ");
      add("notLike", LikeHandler::new, " NOT LIKE ");
      add("andNotLike", " AND ", LikeHandler::new, " NOT LIKE ");
      add("orNotLike", " OR ", LikeHandler::new, " NOT LIKE ");
      add("startsWith", StartsWithHandler::new, " LIKE ");
      add("andStartsWith", " AND ", StartsWithHandler::new, " LIKE ");
      add("orStartsWith", " OR ", StartsWithHandler::new, " LIKE ");
      add("notStartsWith", StartsWithHandler::new, " NOT LIKE ");
      add("andNotStartsWith", " AND ", StartsWithHandler::new, " NOT LIKE ");
      add("orNotStartsWith", " OR ", StartsWithHandler::new, " NOT LIKE ");
      add("endsWith", EndsWithHandler::new, " LIKE ");
      add("andEndsWith", " AND ", EndsWithHandler::new, " LIKE ");
      add("orEndsWith", " OR ", EndsWithHandler::new, " LIKE ");
      add("notEndsWith", EndsWithHandler::new, " NOT LIKE ");
      add("andNotEndsWith", " AND ", EndsWithHandler::new, " NOT LIKE ");
      add("orNotEndsWith", " OR ", EndsWithHandler::new, " NOT LIKE ");
      add("between", BetweenHandler::new);
      add("andBetween", " AND ", BetweenHandler::new);
      add("orBetween", " OR ", BetweenHandler::new);
      add("in", InHandler::new, " IN ");
      add("andIn", " AND ", InHandler::new, " IN ");
      add("orIn", " OR ", InHandler::new, " IN ");
      add("notIn", InHandler::new, " NOT IN ");
      add("andNotIn", " AND ", InHandler::new, " NOT IN ");
      add("orNotIn", " OR ", InHandler::new, " NOT IN ");
      add("isNull", IsNullHandler::new, " IS NULL ");
      add("andIsNull", " AND ", IsNullHandler::new, " IS NULL ");
      add("orIsNull", " OR ", IsNullHandler::new, " IS NULL ");
      add("isNotNull", IsNullHandler::new, " IS NOT NULL ");
      add("andIsNotNull", " AND ", IsNullHandler::new, " IS NOT NULL ");
      add("orIsNotNull", " OR ", IsNullHandler::new, " IS NOT NULL ");
      add("text", TextHandler::new);
      add("import", ImportHandler::new);
      add("choose", ChooseHandler::new);
      add("set", SetHandler::new);
      add("where", WhereHandler::new);
      add("trimWhere", TrimWhereHandler::new);
   }

   private static void initDefaultSpecificationHandlers() {
      add(new EqualsPredicateHandler());
      add(new GreaterThanEqualPredicateHandler());
      add(new GreaterThanPredicateHandler());
      add(new InPredicateHandler());
      add(new IsNotNullPredicateHandler());
      add(new IsNullPredicateHandler());
      add(new JoinPredicateHandler());
      add(new LessThanEqualPredicateHandler());
      add(new LessThanPredicateHandler());
      add(new BetweenPredicateHandler());
      add(new NotBetweenPredicateHandler());
      add(new LikeInPredicateHandler());
      add(new LikeOrLikePredicateHandler());
      add(new LikePredicateHandler());
      add(new NotLikePredicateHandler());
      add(new StartsWithPredicateHandler());
      add(new NotStartsWithPredicateHandler());
      add(new EndsWithPredicateHandler());
      add(new NotEndsWithPredicateHandler());
      add(new LikePatternPredicateHandler());
      add(new NotLikePatternPredicateHandler());
      add(new NotEqualsPredicateHandler());
      add(new NotInPredicateHandler());
      add(new OrEqualsPredicateHandler());
      add(new OrGreaterThanEqualPredicateHandler());
      add(new OrGreaterThanPredicateHandler());
      add(new OrInPredicateHandler());
      add(new OrIsNotNullPredicateHandler());
      add(new OrIsNullPredicateHandler());
      add(new OrLessThanEqualPredicateHandler());
      add(new OrLessThanPredicateHandler());
      add(new OrBetweenPredicateHandler());
      add(new OrNotBetweenPredicateHandler());
      add(new OrLikeOrLikePredicateHandler());
      add(new OrLikePredicateHandler());
      add(new OrNotLikePredicateHandler());
      add(new OrNotEqualsPredicateHandler());
      add(new OrNotInPredicateHandler());
      add(new OrStartsWithPredicateHandler());
      add(new OrNotStartsWithPredicateHandler());
      add(new OrEndsWithPredicateHandler());
      add(new OrNotEndsWithPredicateHandler());
      add(new OrLikePatternPredicateHandler());
      add(new OrNotLikePatternPredicateHandler());
   }

   public FenixConfig setDebug(boolean debug) {
      this.debug = debug;
      return this;
   }

   public FenixConfig setPrintBanner(boolean enabled) {
      this.printBanner = enabled;
      return this;
   }

   public FenixConfig setPrintSqlInfo(boolean enabled) {
      this.printSqlInfo = enabled;
      return this;
   }

   public FenixConfig setXmlLocations(String xmlLocations) {
      this.xmlLocations = xmlLocations;
      return this;
   }

   public FenixConfig setHandlerLocations(String handlerLocations) {
      this.handlerLocations = handlerLocations;
      return this;
   }

   public FenixConfig setUnderscoreTransformerPrefix(String underscoreTransformerPrefix) {
      this.underscoreTransformerPrefix = underscoreTransformerPrefix;
      return this;
   }

   public static void add(String tagName, Class<? extends FenixHandler> handlerCls) {
      tagHandlerMap.put(tagName, new TagHandler(handlerCls));
   }

   public static void add(String tagName, FenixHandlerFactory handlerFactory) {
      tagHandlerMap.put(tagName, new TagHandler(handlerFactory));
   }

   public static void add(String tagName, String prefix, Class<? extends FenixHandler> handlerCls) {
      tagHandlerMap.put(tagName, new TagHandler(prefix, handlerCls));
   }

   public static void add(String tagName, String prefix, FenixHandlerFactory handlerFactory) {
      tagHandlerMap.put(tagName, new TagHandler(prefix, handlerFactory));
   }

   public static void add(String tagName, Class<? extends FenixHandler> handlerCls, String symbol) {
      tagHandlerMap.put(tagName, new TagHandler(handlerCls, symbol));
   }

   public static void add(String tagName, FenixHandlerFactory handlerFactory, String symbol) {
      tagHandlerMap.put(tagName, new TagHandler(handlerFactory, symbol));
   }

   public static void add(String tagName, String prefix, Class<? extends FenixHandler> handlerCls, String symbol) {
      tagHandlerMap.put(tagName, new TagHandler(prefix, handlerCls, symbol));
   }

   public static void add(String tagName, String prefix, FenixHandlerFactory handlerFactory, String symbol) {
      tagHandlerMap.put(tagName, new TagHandler(prefix, handlerFactory, symbol));
   }

   public static void add(Supplier<AbstractPredicateHandler> handlerSupplier) {
      AbstractPredicateHandler handler = (AbstractPredicateHandler)handlerSupplier.get();
      specificationHandlerMap.put(handler.getAnnotation(), handler);
   }

   public static void add(AbstractPredicateHandler handler) {
      specificationHandlerMap.put(handler.getAnnotation(), handler);
   }

   public boolean isDebug() {
      return this.debug;
   }

   public boolean isPrintBanner() {
      return this.printBanner;
   }

   public boolean isPrintSqlInfo() {
      return this.printSqlInfo;
   }

   public String getXmlLocations() {
      return this.xmlLocations;
   }

   public String getHandlerLocations() {
      return this.handlerLocations;
   }

   public String getUnderscoreTransformerPrefix() {
      return this.underscoreTransformerPrefix;
   }

   static {
      initDefaultTagHandler();
      initDefaultSpecificationHandlers();
   }
}
