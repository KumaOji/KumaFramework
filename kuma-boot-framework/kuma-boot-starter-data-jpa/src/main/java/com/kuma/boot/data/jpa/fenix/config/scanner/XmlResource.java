package com.kuma.boot.data.jpa.fenix.config.scanner;

import java.net.URL;
import org.dom4j.Document;

public class XmlResource {
   private String namespace;
   private URL url;
   private Document document;

   public XmlResource(String namespace, Document document) {
      this.namespace = namespace;
      this.document = document;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   public URL getUrl() {
      return this.url;
   }

   public XmlResource setUrl(URL url) {
      this.url = url;
      return this;
   }

   public Document getDocument() {
      return this.document;
   }

   public void setDocument(Document document) {
      this.document = document;
   }
}
