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

   public static class Section implements Map<String, String> {
      private final String name;
      private final Map<String, String> props;

      private Section(String name) {
         if (name == null) {
            throw new NullPointerException("name");
         } else {
            this.name = name;
            this.props = new LinkedHashMap();
         }
      }

      private Section(String name, String sectionContent) {
         if (name == null) {
            throw new NullPointerException("name");
         } else {
            this.name = name;
            Map<String, String> props;
            if (!StringUtils.isEmpty(sectionContent)) {
               props = toMapProps(sectionContent);
            } else {
               props = new LinkedHashMap();
            }

            if (props != null) {
               this.props = props;
            } else {
               this.props = new LinkedHashMap();
            }

         }
      }

      private Section(Section defaults) {
         this(defaults.getName());
         this.putAll(defaults.props);
      }

      protected static boolean isContinued(String line) {
         if (!StringUtils.isEmpty(line)) {
            return false;
         } else {
            int length = line.length();
            int backslashCount = 0;

            for(int i = length - 1; i > 0 && line.charAt(i) == '\\'; --i) {
               ++backslashCount;
            }

            return backslashCount % 2 != 0;
         }
      }

      private static boolean isKeyValueSeparatorChar(char c) {
         return Character.isWhitespace(c) || c == ':' || c == '=';
      }

      private static boolean isCharEscaped(CharSequence s, int index) {
         return index > 0 && s.charAt(index - 1) == '\\';
      }

      protected static String[] splitKeyValue(String keyValueLine) {
         String line = StringUtils.trimToNull(keyValueLine);
         if (line == null) {
            return null;
         } else {
            StringBuilder keyBuffer = new StringBuilder();
            StringBuilder valueBuffer = new StringBuilder();
            boolean buildingKey = true;

            for(int i = 0; i < line.length(); ++i) {
               char c = line.charAt(i);
               if (buildingKey) {
                  if (isKeyValueSeparatorChar(c) && !isCharEscaped(line, i)) {
                     buildingKey = false;
                  } else {
                     keyBuffer.append(c);
                  }
               } else if (valueBuffer.length() != 0 || !isKeyValueSeparatorChar(c) || isCharEscaped(line, i)) {
                  valueBuffer.append(c);
               }
            }

            String key = StringUtils.trimToNull(keyBuffer.toString());
            String value = StringUtils.trimToNull(valueBuffer.toString());
            if (key != null && value != null) {
               LogUtils.info("Discovered key/value pair: {}={}", new Object[]{key, value});
               return new String[]{key, value};
            } else {
               String msg = "Line argument must contain a key and a value.  Only one string token was found.";
               throw new IllegalArgumentException(msg);
            }
         }
      }

      private static Map<String, String> toMapProps(String content) {
         Map<String, String> props = new LinkedHashMap();
         StringBuilder lineBuffer = new StringBuilder();
         Scanner scanner = new Scanner(content);

         while(scanner.hasNextLine()) {
            String line = StringUtils.trimToNull(scanner.nextLine());
            if (isContinued(line)) {
               line = line.substring(0, line.length() - 1);
               lineBuffer.append(line);
            } else {
               lineBuffer.append(line);
               line = lineBuffer.toString();
               lineBuffer = new StringBuilder();
               String[] kvPair = splitKeyValue(line);
               props.put(kvPair[0], kvPair[1]);
            }
         }

         return props;
      }

      public String getName() {
         return this.name;
      }

      public void clear() {
         this.props.clear();
      }

      public boolean containsKey(Object key) {
         return this.props.containsKey(key);
      }

      public boolean containsValue(Object value) {
         return this.props.containsValue(value);
      }

      public Set<Map.Entry<String, String>> entrySet() {
         return this.props.entrySet();
      }

      public String get(Object key) {
         return (String)this.props.get(key);
      }

      public boolean isEmpty() {
         return this.props.isEmpty();
      }

      public Set<String> keySet() {
         return this.props.keySet();
      }

      public String put(String key, String value) {
         return (String)this.props.put(key, value);
      }

      public void putAll(Map<? extends String, ? extends String> m) {
         this.props.putAll(m);
      }

      public String remove(Object key) {
         return (String)this.props.remove(key);
      }

      public int size() {
         return this.props.size();
      }

      public Collection<String> values() {
         return this.props.values();
      }

      public String toString() {
         String name = this.getName();
         return "".equals(name) ? "<default>" : name;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Section other)) {
            return false;
         } else {
            return this.getName().equals(other.getName()) && this.props.equals(other.props);
         }
      }

      public int hashCode() {
         return this.name.hashCode() * 31 + this.props.hashCode();
      }
   }
}
