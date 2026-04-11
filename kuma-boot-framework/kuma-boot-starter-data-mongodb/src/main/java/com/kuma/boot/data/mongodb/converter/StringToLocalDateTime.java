package com.kuma.boot.data.mongodb.converter;

import com.kuma.boot.common.utils.date.DateUtils;
import java.time.LocalDateTime;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToLocalDateTime implements Converter<String, LocalDateTime> {
   public StringToLocalDateTime() {
   }

   public LocalDateTime convert(String source) {
      return LocalDateTime.parse(source, DateUtils.DATETIME_FORMATTER);
   }
}
