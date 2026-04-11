package com.kuma.boot.sensitive.sensitiveword.bs;

import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWord;
import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWordCharIgnore;
import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWordDestroy;
import com.kuma.boot.sensitive.sensitiveword.api.IWordAllow;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordData;
import com.kuma.boot.sensitive.sensitiveword.api.IWordDeny;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;
import com.kuma.boot.sensitive.sensitiveword.api.IWordReplace;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResultCondition;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResultHandler;
import com.kuma.boot.sensitive.sensitiveword.api.IWordTag;
import com.kuma.boot.sensitive.sensitiveword.api.combine.IWordAllowDenyCombine;
import com.kuma.boot.sensitive.sensitiveword.api.combine.IWordCheckCombine;
import com.kuma.boot.sensitive.sensitiveword.api.combine.IWordFormatCombine;
import com.kuma.boot.sensitive.sensitiveword.core.SensitiveWords;
import com.kuma.boot.sensitive.sensitiveword.support.allow.WordAllows;
import com.kuma.boot.sensitive.sensitiveword.support.check.WordChecks;
import com.kuma.boot.sensitive.sensitiveword.support.combine.allowdeny.WordAllowDenyCombines;
import com.kuma.boot.sensitive.sensitiveword.support.combine.check.WordCheckCombines;
import com.kuma.boot.sensitive.sensitiveword.support.combine.format.WordFormatCombines;
import com.kuma.boot.sensitive.sensitiveword.support.data.WordDatas;
import com.kuma.boot.sensitive.sensitiveword.support.deny.WordDenys;
import com.kuma.boot.sensitive.sensitiveword.support.ignore.SensitiveWordCharIgnores;
import com.kuma.boot.sensitive.sensitiveword.support.replace.WordReplaces;
import com.kuma.boot.sensitive.sensitiveword.support.result.WordResultHandlers;
import com.kuma.boot.sensitive.sensitiveword.support.resultcondition.WordResultConditions;
import com.kuma.boot.sensitive.sensitiveword.support.tag.WordTags;
import com.kuma.boot.sensitive.sensitiveword.utils.InnerWordFormatUtils;
import com.kuma.boot.sensitive.sensitiveword.utils.InnerWordTagUtils;
import com.xkzhangsan.time.utils.CollectionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SensitiveWordBs implements ISensitiveWordDestroy {
   private boolean ignoreCase = true;
   private boolean ignoreWidth = true;
   private boolean ignoreNumStyle = true;
   private boolean ignoreChineseStyle = true;
   private boolean ignoreEnglishStyle = true;
   private boolean ignoreRepeat = false;
   private boolean wordFailFast = true;
   private boolean enableNumCheck = false;
   private boolean enableEmailCheck = false;
   private boolean enableUrlCheck = false;
   private boolean enableWordCheck = true;
   private boolean enableIpv4Check = false;
   private int numCheckLen = 8;
   private ISensitiveWord sensitiveWord = SensitiveWords.defaults();
   private IWordData wordData = WordDatas.defaults();
   private IWordData wordDataAllow = WordDatas.defaults();
   private IWordDeny wordDeny = WordDenys.defaults();
   private IWordAllow wordAllow = WordAllows.defaults();
   private IWordReplace wordReplace = WordReplaces.defaults();
   private IWordContext context = SensitiveWordContext.newInstance();
   private IWordCheckCombine wordCheckCombine = WordCheckCombines.defaults();
   private IWordFormatCombine wordFormatCombine = WordFormatCombines.defaults();
   private IWordAllowDenyCombine wordAllowDenyCombine = WordAllowDenyCombines.defaults();
   private IWordTag wordTag = WordTags.defaults();
   private ISensitiveWordCharIgnore charIgnore = SensitiveWordCharIgnores.defaults();
   private IWordResultCondition wordResultCondition = WordResultConditions.alwaysTrue();
   private IWordCheck wordCheckWord = WordChecks.word();
   private IWordCheck wordCheckNum = WordChecks.num();
   private IWordCheck wordCheckEmail = WordChecks.email();
   private IWordCheck wordCheckUrl = WordChecks.url();
   private IWordCheck wordCheckIpv4 = WordChecks.ipv4();

   private SensitiveWordBs() {
   }

   public static SensitiveWordBs newInstance() {
      return new SensitiveWordBs();
   }

   public SensitiveWordBs init() {
      IWordContext context = this.initContext();
      IWordFormat charFormat = this.wordFormatCombine.initWordFormat(context);
      context.wordFormat(charFormat);
      IWordCheck sensitiveCheck = this.wordCheckCombine.initWordCheck(context);
      context.sensitiveCheck(sensitiveCheck);
      List<String> wordAllowList = this.wordAllow.allow();
      List<String> wordDenyList = this.wordDeny.deny();
      Collection<String> denyList = this.wordAllowDenyCombine.getActualDenyList(wordAllowList, wordDenyList, context);
      this.wordData.initWordData(denyList);
      List<String> actualAllowList = InnerWordFormatUtils.formatWordList(wordAllowList, context);
      this.wordDataAllow.initWordData(actualAllowList);
      context.wordData(this.wordData);
      context.wordDataAllow(this.wordDataAllow);
      this.context = context;
      return this;
   }

   private IWordContext initContext() {
      IWordContext context = SensitiveWordContext.newInstance();
      context.ignoreCase(this.ignoreCase);
      context.ignoreWidth(this.ignoreWidth);
      context.ignoreNumStyle(this.ignoreNumStyle);
      context.ignoreChineseStyle(this.ignoreChineseStyle);
      context.ignoreEnglishStyle(this.ignoreEnglishStyle);
      context.ignoreRepeat(this.ignoreRepeat);
      context.wordFailFast(this.wordFailFast);
      context.enableNumCheck(this.enableNumCheck);
      context.enableEmailCheck(this.enableEmailCheck);
      context.enableUrlCheck(this.enableUrlCheck);
      context.enableWordCheck(this.enableWordCheck);
      context.enableIpv4Check(this.enableIpv4Check);
      context.wordCheckWord(this.wordCheckWord);
      context.wordCheckEmail(this.wordCheckEmail);
      context.wordCheckNum(this.wordCheckNum);
      context.wordCheckUrl(this.wordCheckUrl);
      context.wordCheckIpv4(this.wordCheckIpv4);
      context.sensitiveCheckNumLen(this.numCheckLen);
      context.wordReplace(this.wordReplace);
      context.wordData(this.wordData);
      context.wordTag(this.wordTag);
      context.charIgnore(this.charIgnore);
      context.wordResultCondition(this.wordResultCondition);
      return context;
   }

   public SensitiveWordBs wordResultCondition(IWordResultCondition wordResultCondition) {
      ArgUtils.notNull(wordResultCondition, "wordResultCondition");
      this.wordResultCondition = wordResultCondition;
      return this;
   }

   public SensitiveWordBs charIgnore(ISensitiveWordCharIgnore charIgnore) {
      ArgUtils.notNull(charIgnore, "charIgnore");
      this.charIgnore = charIgnore;
      return this;
   }

   public SensitiveWordBs wordTag(IWordTag wordTag) {
      ArgUtils.notNull(wordTag, "wordTag");
      this.wordTag = wordTag;
      return this;
   }

   public SensitiveWordBs wordCheckCombine(IWordCheckCombine wordCheckCombine) {
      ArgUtils.notNull(wordCheckCombine, "wordCheckCombine");
      this.wordCheckCombine = wordCheckCombine;
      return this;
   }

   public SensitiveWordBs wordFormatCombine(IWordFormatCombine wordFormatCombine) {
      ArgUtils.notNull(wordFormatCombine, "wordFormatCombine");
      this.wordFormatCombine = wordFormatCombine;
      return this;
   }

   public SensitiveWordBs wordAllowDenyCombine(IWordAllowDenyCombine wordAllowDenyCombine) {
      ArgUtils.notNull(wordAllowDenyCombine, "wordAllowDenyCombine");
      this.wordAllowDenyCombine = wordAllowDenyCombine;
      return this;
   }

   public SensitiveWordBs wordData(IWordData wordData) {
      ArgUtils.notNull(wordData, "wordData");
      this.wordData = wordData;
      return this;
   }

   public SensitiveWordBs wordDataAllow(IWordData wordDataAllow) {
      ArgUtils.notNull(wordDataAllow, "wordDataAllow");
      this.wordDataAllow = wordDataAllow;
      return this;
   }

   public SensitiveWordBs sensitiveWord(ISensitiveWord sensitiveWord) {
      ArgUtils.notNull(sensitiveWord, "sensitiveWord");
      this.sensitiveWord = sensitiveWord;
      return this;
   }

   public SensitiveWordBs wordReplace(IWordReplace wordReplace) {
      ArgUtils.notNull(wordReplace, "wordReplace");
      this.wordReplace = wordReplace;
      return this;
   }

   public SensitiveWordBs wordDeny(IWordDeny wordDeny) {
      ArgUtils.notNull(wordDeny, "wordDeny");
      this.wordDeny = wordDeny;
      return this;
   }

   public SensitiveWordBs wordAllow(IWordAllow wordAllow) {
      ArgUtils.notNull(wordAllow, "wordAllow");
      this.wordAllow = wordAllow;
      return this;
   }

   public SensitiveWordBs wordCheckWord(IWordCheck wordCheckWord) {
      ArgUtils.notNull(wordCheckWord, "wordCheckWord");
      this.wordCheckWord = wordCheckWord;
      return this;
   }

   public SensitiveWordBs wordCheckNum(IWordCheck wordCheckNum) {
      ArgUtils.notNull(wordCheckNum, "wordCheckNum");
      this.wordCheckNum = wordCheckNum;
      return this;
   }

   public SensitiveWordBs wordCheckEmail(IWordCheck wordCheckEmail) {
      ArgUtils.notNull(wordCheckEmail, "wordCheckEmail");
      this.wordCheckEmail = wordCheckEmail;
      return this;
   }

   public SensitiveWordBs wordCheckUrl(IWordCheck wordCheckUrl) {
      ArgUtils.notNull(wordCheckUrl, "wordCheckUrl");
      this.wordCheckUrl = wordCheckUrl;
      return this;
   }

   public SensitiveWordBs wordCheckIpv4(IWordCheck wordCheckIpv4) {
      ArgUtils.notNull(wordCheckIpv4, "wordCheckIpv4");
      this.wordCheckIpv4 = wordCheckIpv4;
      return this;
   }

   public SensitiveWordBs enableIpv4Check(boolean enableIpv4Check) {
      this.enableIpv4Check = enableIpv4Check;
      return this;
   }

   public SensitiveWordBs enableWordCheck(boolean enableWordCheck) {
      this.enableWordCheck = enableWordCheck;
      return this;
   }

   public SensitiveWordBs enableNumCheck(boolean enableNumCheck) {
      this.enableNumCheck = enableNumCheck;
      return this;
   }

   public SensitiveWordBs numCheckLen(int numCheckLen) {
      this.numCheckLen = numCheckLen;
      return this;
   }

   public SensitiveWordBs enableEmailCheck(boolean enableEmailCheck) {
      this.enableEmailCheck = enableEmailCheck;
      return this;
   }

   public SensitiveWordBs enableUrlCheck(boolean enableUrlCheck) {
      this.enableUrlCheck = enableUrlCheck;
      return this;
   }

   public SensitiveWordBs ignoreCase(boolean ignoreCase) {
      this.ignoreCase = ignoreCase;
      return this;
   }

   public SensitiveWordBs ignoreWidth(boolean ignoreWidth) {
      this.ignoreWidth = ignoreWidth;
      return this;
   }

   public SensitiveWordBs ignoreNumStyle(boolean ignoreNumStyle) {
      this.ignoreNumStyle = ignoreNumStyle;
      return this;
   }

   public SensitiveWordBs ignoreChineseStyle(boolean ignoreChineseStyle) {
      this.ignoreChineseStyle = ignoreChineseStyle;
      return this;
   }

   public SensitiveWordBs ignoreEnglishStyle(boolean ignoreEnglishStyle) {
      this.ignoreEnglishStyle = ignoreEnglishStyle;
      return this;
   }

   public SensitiveWordBs ignoreRepeat(boolean ignoreRepeat) {
      this.ignoreRepeat = ignoreRepeat;
      return this;
   }

   public SensitiveWordBs wordFailFast(boolean wordFailFast) {
      this.wordFailFast = wordFailFast;
      return this;
   }

   public boolean contains(final String target) {
      return this.sensitiveWord.contains(target, this.context);
   }

   public List<String> findAll(final String target) {
      return this.<String>findAll(target, WordResultHandlers.word());
   }

   public String findFirst(final String target) {
      return (String)this.findFirst(target, WordResultHandlers.word());
   }

   public <R> List<R> findAll(final String target, final IWordResultHandler<R> handler) {
      ArgUtils.notNull(handler, "handler");
      List<IWordResult> wordResults = this.sensitiveWord.findAll(target, this.context);
      return CollectionUtils.toList(wordResults, new Handler<IWordResult, R>() {
         {
            Objects.requireNonNull(SensitiveWordBs.this);
         }

         public R handle(IWordResult wordResult) {
            return handler.handle(wordResult, SensitiveWordBs.this.context, target);
         }
      });
   }

   public <R> R findFirst(final String target, final IWordResultHandler<R> handler) {
      ArgUtils.notNull(handler, "handler");
      IWordResult wordResult = this.sensitiveWord.findFirst(target, this.context);
      return handler.handle(wordResult, this.context, target);
   }

   public String replace(final String target) {
      return this.sensitiveWord.replace(target, this.context);
   }

   public Set<String> tags(final String word) {
      return InnerWordTagUtils.tags(word, this.context);
   }

   public void destroy() {
      this.wordData.destroy();
      this.wordDataAllow.destroy();
   }

   public void removeWord(String word, String... others) {
      List<String> wordList = new ArrayList();
      wordList.add(word);
      wordList.addAll(Arrays.asList(others));
      this.removeWord(wordList);
   }

   public void removeWord(Collection<String> collection) {
      if (!CollectionUtil.isEmpty(collection)) {
         List<String> formatList = InnerWordFormatUtils.formatWordList(collection, this.context);
         this.wordData.removeWord(formatList);
      }
   }

   public void addWord(Collection<String> collection) {
      List<String> formatAllowList = InnerWordFormatUtils.formatWordList(collection, this.context);
      this.wordData.addWord(formatAllowList);
   }

   public void addWord(String word, String... others) {
      List<String> wordList = new ArrayList();
      wordList.add(word);
      wordList.addAll(Arrays.asList(others));
      this.addWord(wordList);
   }

   public void removeWordAllow(String word, String... others) {
      List<String> wordList = new ArrayList();
      wordList.add(word);
      wordList.addAll(Arrays.asList(others));
      this.removeWordAllow(wordList);
   }

   public void removeWordAllow(Collection<String> collection) {
      if (!CollectionUtil.isEmpty(collection)) {
         List<String> formatList = InnerWordFormatUtils.formatWordList(collection, this.context);
         this.wordDataAllow.removeWord(formatList);
      }
   }

   public void addWordAllow(Collection<String> collection) {
      List<String> formatList = InnerWordFormatUtils.formatWordList(collection, this.context);
      this.wordDataAllow.addWord(formatList);
   }

   public void addWordAllow(String word, String... others) {
      List<String> wordList = new ArrayList();
      wordList.add(word);
      wordList.addAll(Arrays.asList(others));
      this.addWordAllow(wordList);
   }
}
