package com.kuma.boot.sensitive.sensitiveword.support.deny;

import com.kuma.boot.sensitive.sensitiveword.api.IWordDeny;
import java.util.ArrayList;
import java.util.List;

public class WordDenyEmpty implements IWordDeny {
   public WordDenyEmpty() {
   }

   public List<String> deny() {
      return new ArrayList();
   }
}
