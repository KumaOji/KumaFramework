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

   public static class Movie extends AbstractProvider<Faker> {
      private static final String[] MOVIE_NAMES = new String[]{"肖申克的救赎", "霸王别姬", "阿甘正传", "泰坦尼克号", "这个杀手不太冷", "美丽人生", "千与千寻", "辛德勒的名单", "盗梦空间", "忠犬八公的故事"};

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

      @SuppressWarnings("unchecked")
      public Movie myMovie() {
         return (Movie)this.getProvider(Movie.class, (pr) -> new Movie(this));
      }
   }
}
