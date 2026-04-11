package com.kuma.boot.sensitive.sensitiveword.support.allow;

import com.kuma.boot.sensitive.sensitiveword.api.IWordAllow;
import java.util.ArrayList;
import java.util.List;

public class WordAllowEmpty implements IWordAllow {
   public WordAllowEmpty() {
   }

   public List<String> allow() {
      return new ArrayList();
   }
}
