package com.kuma.boot.logger.logback.layout;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class MaskingPatternLogbackLayout extends TraceIdPatternLogbackLayout {
   private Pattern multilinePattern;
   private final List<String> maskPatterns = new ArrayList();

   public MaskingPatternLogbackLayout() {
   }

   public void addMaskPattern(String maskPattern) {
      this.maskPatterns.add(maskPattern);
      this.multilinePattern = Pattern.compile(String.join("|", this.maskPatterns), 8);
   }

   public String doLayout(ILoggingEvent event) {
      return this.maskMessage(super.doLayout(event));
   }

   private String maskMessage(String message) {
      if (this.multilinePattern == null) {
         return message;
      } else {
         StringBuilder sb = new StringBuilder(message);
         Matcher matcher = this.multilinePattern.matcher(sb);

         while(matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach((group) -> {
               if (matcher.group(group) != null) {
                  IntStream.range(matcher.start(group), matcher.end(group)).forEach((i) -> sb.setCharAt(i, '*'));
               }

            });
         }

         return sb.toString();
      }
   }
}
