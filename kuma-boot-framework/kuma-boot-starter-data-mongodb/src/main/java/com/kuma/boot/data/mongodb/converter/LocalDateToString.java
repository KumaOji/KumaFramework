package com.kuma.boot.data.mongodb.converter;

import com.kuma.boot.common.utils.date.DateUtils;
import java.time.LocalDateTime;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class LocalDateToString implements Converter<LocalDateTime, String> {
   public LocalDateToString() {
   }

   public String convert(LocalDateTime source) {
      return source.format(DateUtils.DATE_FORMATTER);
   }
}
