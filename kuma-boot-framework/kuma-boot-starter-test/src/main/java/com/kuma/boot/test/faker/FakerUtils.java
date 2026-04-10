package com.kuma.boot.test.faker;

import java.util.Locale;
import net.datafaker.Faker;
import net.datafaker.providers.base.AbstractProvider;

public class FakerUtils {
   public FakerUtils() {
   }

   public static Faker faker() {
      return new Faker(Locale.CHINA);
   }

   public static class Movie extends AbstractProvider {
      private static final String[] MOVIE_NAMES = new String[]{"\u8096\u7533\u514b\u7684\u6551\u8d4e", "\u9738\u738b\u522b\u59ec", "\u963f\u7518\u6b63\u4f20", "\u6cf0\u5766\u5c3c\u514b\u53f7", "\u8fd9\u4e2a\u6740\u624b\u4e0d\u592a\u51b7", "\u7f8e\u4e3d\u4eba\u751f", "\u5343\u4e0e\u5343\u5bfb", "\u8f9b\u5fb7\u52d2\u7684\u540d\u5355", "\u76d7\u68a6\u7a7a\u95f4", "\u5fe0\u72ac\u516b\u516c\u7684\u6545\u4e8b"};

      public Movie(Faker faker) {
         super(faker);
      }

      public String movie() {
         return MOVIE_NAMES[this.faker.random().nextInt(MOVIE_NAMES.length)];
      }
   }

   public static class MyCustomFaker extends Faker {
      public MyCustomFaker(Locale locale) {
         super(locale);
      }

      public Movie myMovie() {
         return (Movie)this.getProvider(Movie.class, (pr) -> new Movie(this));
      }
   }
}
