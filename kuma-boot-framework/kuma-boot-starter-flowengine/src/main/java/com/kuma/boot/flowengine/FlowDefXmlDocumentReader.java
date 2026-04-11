package com.kuma.boot.flowengine;

import com.kuma.boot.flowengine.exception.FlowException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FlowDefXmlDocumentReader {
   public static final String ACTIVITY_XSD_PATH = "/META-INF/kit/flow_def.xsd";
   private DocumentBuilder domBuilder;
   private Validator validator;
   private FlowContext flowContext;
   private FlowDefParser parser = new FlowDefParser();

   public FlowDefXmlDocumentReader(FlowContext flowContext) {
      try {
         this.flowContext = flowContext;
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         builderFactory.setNamespaceAware(true);
         this.domBuilder = builderFactory.newDocumentBuilder();
         SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
         ClassPathResource resource = new ClassPathResource("/META-INF/kit/flow_def.xsd");
         Schema schema = schemaFactory.newSchema(resource.getURL());
         this.validator = schema.newValidator();
      } catch (SAXException | IOException | ParserConfigurationException e) {
         throw new FlowException("\u6784\u5efaXML\u89e3\u6790\u5bf9\u8c61\u8fc7\u7a0b\u4e2d\u51fa\u73b0\u9519\u8bef", e);
      }
   }

   public void analyze(Resource resource) {
      this.validate(resource);
      Document document = this.createDocument(resource);
      this.parse(document);
   }

   private void parse(Document document) {
      this.flowContext.registry(this.parser.parse(document.getDocumentElement()));
   }

   private Document createDocument(Resource resource) {
      try {
         Document document = this.domBuilder.parse(resource.getInputStream());
         return document;
      } catch (SAXException e) {
         throw new FlowException("\u5206\u6790xml\u6587\u4ef6\u51fa\u9519...", e);
      } catch (IOException e) {
         throw new FlowException("\u52a0\u8f7dxml\u6587\u4ef6\u5931\u8d25...", e);
      }
   }

   private void validate(Resource resource) {
      try {
         Source source = new StreamSource(resource.getURL().openStream());
         this.validator.validate(source);
      } catch (SAXException e) {
         throw new FlowException("xml\u9a8c\u8bc1\u5931\u8d25...", e);
      } catch (IOException var4) {
         throw new FlowException("\u52a0\u8f7dxml\u6587\u4ef6\u5931\u8d25..");
      }
   }
}
