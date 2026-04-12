package com.kuma.boot.eventbus.disruptor.tmp3.context;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.exception.EventHandleException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Ini implements Map<String, Section> {
   public static final String DEFAULT_SECTION_NAME = "";
   public static final String DEFAULT_CHARSET_NAME = "UTF-8";
   public static final String COMMENT_POUND = "#";
   public static final String COMMENT_SEMICOLON = ";";
   public static final String SECTION_PREFIX = "[";
   public static final String SECTION_SUFFIX = "]";
   protected static final char ESCAPE_TOKEN = '\\';
   private final Map<String, Section> sections;

   public Ini() {
      this.sections = new LinkedHashMap();
   }

   public Ini(Ini defaults) {
      this();
      if (defaults == null) {
         throw new NullPointerException("Defaults cannot be null.");
      } else {
         for(Section section : defaults.getSections()) {
            Section copy = new Section(section);
            this.sections.put(section.getName(), copy);
         }

      }
   }

   public boolean isEmpty() {
      Collection<Section> sections = this.sections.values();
      if (!sections.isEmpty()) {
         for(Section section : sections) {
            if (!section.isEmpty()) {
               return false;
            }
         }
      }

      return true;
   }

   public Set<String> getSectionNames() {
      return Collections.unmodifiableSet(this.sections.keySet());
   }

   public Collection<Section> getSections() {
      return Collections.unmodifiableCollection(this.sections.values());
   }

   public Section getSection(String sectionName) {
      String name = cleanName(sectionName);
      return (Section)this.sections.get(name);
   }

   public Section addSection(String sectionName) {
      String name = cleanName(sectionName);
      Section section = this.getSection(name);
      if (section == null) {
         section = new Section(name);
         this.sections.put(name, section);
      }

      return section;
   }

   public Section removeSection(String sectionName) {
      String name = cleanName(sectionName);
      return (Section)this.sections.remove(name);
   }

   private static String cleanName(String sectionName) {
      String name = StringUtils.trimToNull(sectionName);
      if (name == null) {
         LogUtils.info("Specified name was null or empty.  Defaulting to the default section (name = \"\")", new Object[0]);
         name = "";
      }

      return name;
   }

   public void setSectionProperty(String sectionName, String propertyName, String propertyValue) {
      String name = cleanName(sectionName);
      Section section = this.getSection(name);
      if (section == null) {
         section = this.addSection(name);
      }

      section.put(propertyName, propertyValue);
   }

   public String getSectionProperty(String sectionName, String propertyName) {
      Section section = this.getSection(sectionName);
      return section != null ? section.get(propertyName) : null;
   }

   public String getSectionProperty(String sectionName, String propertyName, String defaultValue) {
      String value = this.getSectionProperty(sectionName, propertyName);
      return value != null ? value : defaultValue;
   }

   public void load(String iniConfig) throws EventHandleException {
      this.load(new Scanner(iniConfig));
   }

   public void load(InputStream is) throws IOException {
      if (is == null) {
         throw new NullPointerException("InputStream argument cannot be null.");
      } else {
         InputStreamReader isr;
         try {
            isr = new InputStreamReader(is, "UTF-8");
         } catch (UnsupportedEncodingException e) {
            throw new EventHandleException(e);
         }

         this.load((Reader)isr);
      }
   }

   public void load(Reader reader) {
      Scanner scanner = new Scanner(reader);

      try {
         this.load(scanner);
      } finally {
         try {
            scanner.close();
         } catch (Exception e) {
            LogUtils.debug("Unable to cleanly close the InputStream scanner.  Non-critical - ignoring.", new Object[]{e});
         }

      }

   }

   private void addSection(String name, StringBuilder content) {
      if (content.length() > 0) {
         String contentString = content.toString();
         String cleaned = StringUtils.trimToNull(contentString);
         if (cleaned != null) {
            Section section = new Section(name, contentString);
            if (!section.isEmpty()) {
               this.sections.put(name, section);
            }
         }
      }

   }

   public void load(Scanner scanner) {
      String sectionName = "";
      StringBuilder sectionContent = new StringBuilder();

      while(scanner.hasNextLine()) {
         String rawLine = scanner.nextLine();
         String line = StringUtils.trimToNull(rawLine);
         if (line != null && !line.startsWith("#") && !line.startsWith(";")) {
            String newSectionName = getSectionName(line);
            if (newSectionName != null) {
               this.addSection(sectionName, sectionContent);
               sectionContent = new StringBuilder();
               sectionName = newSectionName;
               LogUtils.debug("Parsing [" + newSectionName + "]", new Object[0]);
            } else {
               sectionContent.append(rawLine).append("\n");
            }
         }
      }

      this.addSection(sectionName, sectionContent);
   }

   protected static boolean isSectionHeader(String line) {
      String s = StringUtils.trimToNull(line);
      return s.startsWith("[") && s.endsWith("]");
   }

   protected static String getSectionName(String line) {
      String s = StringUtils.trimToNull(line);
      return isSectionHeader(s) ? cleanName(s.substring(1, s.length() - 1)) : null;
   }

   public boolean equals(Object obj) {
      if (obj instanceof Ini ini) {
         return this.sections.equals(ini.sections);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.sections.hashCode();
   }

   public String toString() {
      if (this.sections != null && !this.sections.isEmpty()) {
         StringBuilder sb = new StringBuilder("sections=");
         int i = 0;

         for(Section section : this.sections.values()) {
            if (i > 0) {
               sb.append(",");
            }

            sb.append(section.toString());
            ++i;
         }

         return sb.toString();
      } else {
         return "<empty INI>";
      }
   }

   public int size() {
      return this.sections.size();
   }

   public boolean containsKey(Object key) {
      return this.sections.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.sections.containsValue(value);
   }

   public Section get(Object key) {
      return (Section)this.sections.get(key);
   }

   public Section put(String key, Section value) {
      return (Section)this.sections.put(key, value);
   }

   public Section remove(Object key) {
      return (Section)this.sections.remove(key);
   }

   public void putAll(Map<? extends String, ? extends Section> m) {
      this.sections.putAll(m);
   }

   public void clear() {
      this.sections.clear();
   }

   public Set<String> keySet() {
      return Collections.unmodifiableSet(this.sections.keySet());
   }

   public Collection<Section> values() {
      return Collections.unmodifiableCollection(this.sections.values());
   }

   public Set<Map.Entry<String, Section>> entrySet() {
      return Collections.unmodifiableSet(this.sections.entrySet());
   }
}
