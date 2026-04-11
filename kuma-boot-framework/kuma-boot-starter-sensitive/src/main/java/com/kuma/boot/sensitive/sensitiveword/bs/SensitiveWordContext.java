package com.kuma.boot.sensitive.sensitiveword.bs;

import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWordCharIgnore;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordData;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;
import com.kuma.boot.sensitive.sensitiveword.api.IWordReplace;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResultCondition;
import com.kuma.boot.sensitive.sensitiveword.api.IWordTag;

public class SensitiveWordContext implements IWordContext {
   private boolean wordFailFast;
   private boolean ignoreCase;
   private boolean ignoreWidth;
   private boolean ignoreNumStyle;
   private boolean enableWordCheck;
   private boolean enableNumCheck;
   private boolean ignoreChineseStyle;
   private boolean ignoreEnglishStyle;
   private boolean ignoreRepeat;
   private boolean enableEmailCheck;
   private boolean enableUrlCheck;
   private boolean enableIpv4Check;
   private int sensitiveCheckNumLen;
   private IWordCheck wordCheck;
   private IWordReplace wordReplace;
   private IWordFormat wordFormat;
   private IWordData wordData;
   private IWordData wordDataAllow;
   private IWordTag wordTag;
   private ISensitiveWordCharIgnore charIgnore;
   private IWordResultCondition wordResultCondition;
   private IWordCheck wordCheckWord;
   private IWordCheck wordCheckNum;
   private IWordCheck wordCheckEmail;
   private IWordCheck wordCheckUrl;
   private IWordCheck wordCheckIpv4;

   public IWordData wordData() {
      return this.wordData;
   }

   public SensitiveWordContext wordData(IWordData wordData) {
      this.wordData = wordData;
      return this;
   }

   public IWordData wordDataAllow() {
      return this.wordDataAllow;
   }

   public SensitiveWordContext wordDataAllow(IWordData wordDataAllow) {
      this.wordDataAllow = wordDataAllow;
      return this;
   }

   public IWordReplace wordReplace() {
      return this.wordReplace;
   }

   public SensitiveWordContext wordReplace(IWordReplace wordReplace) {
      this.wordReplace = wordReplace;
      return this;
   }

   public IWordCheck sensitiveCheck() {
      return this.wordCheck;
   }

   public SensitiveWordContext sensitiveCheck(IWordCheck sensitiveCheck) {
      this.wordCheck = sensitiveCheck;
      return this;
   }

   private SensitiveWordContext() {
   }

   public static SensitiveWordContext newInstance() {
      return new SensitiveWordContext();
   }

   public boolean wordFailFast() {
      return this.wordFailFast;
   }

   public IWordContext wordFailFast(boolean wordFailFast) {
      this.wordFailFast = wordFailFast;
      return this;
   }

   public boolean ignoreCase() {
      return this.ignoreCase;
   }

   public SensitiveWordContext ignoreCase(boolean ignoreCase) {
      this.ignoreCase = ignoreCase;
      return this;
   }

   public boolean ignoreWidth() {
      return this.ignoreWidth;
   }

   public SensitiveWordContext ignoreWidth(boolean ignoreWidth) {
      this.ignoreWidth = ignoreWidth;
      return this;
   }

   public boolean ignoreNumStyle() {
      return this.ignoreNumStyle;
   }

   public SensitiveWordContext ignoreNumStyle(boolean ignoreNumStyle) {
      this.ignoreNumStyle = ignoreNumStyle;
      return this;
   }

   public boolean enableWordCheck() {
      return this.enableWordCheck;
   }

   public SensitiveWordContext enableWordCheck(boolean enableWordCheck) {
      this.enableWordCheck = enableWordCheck;
      return this;
   }

   public boolean enableNumCheck() {
      return this.enableNumCheck;
   }

   public SensitiveWordContext enableNumCheck(boolean enableNumCheck) {
      this.enableNumCheck = enableNumCheck;
      return this;
   }

   public boolean enableIpv4Check() {
      return this.enableIpv4Check;
   }

   public SensitiveWordContext enableIpv4Check(boolean enableIpv4Check) {
      this.enableIpv4Check = enableIpv4Check;
      return this;
   }

   public boolean ignoreChineseStyle() {
      return this.ignoreChineseStyle;
   }

   public SensitiveWordContext ignoreChineseStyle(boolean ignoreChineseStyle) {
      this.ignoreChineseStyle = ignoreChineseStyle;
      return this;
   }

   public boolean ignoreEnglishStyle() {
      return this.ignoreEnglishStyle;
   }

   public SensitiveWordContext ignoreEnglishStyle(boolean ignoreEnglishStyle) {
      this.ignoreEnglishStyle = ignoreEnglishStyle;
      return this;
   }

   public boolean ignoreRepeat() {
      return this.ignoreRepeat;
   }

   public SensitiveWordContext ignoreRepeat(boolean ignoreRepeat) {
      this.ignoreRepeat = ignoreRepeat;
      return this;
   }

   public boolean enableEmailCheck() {
      return this.enableEmailCheck;
   }

   public SensitiveWordContext enableEmailCheck(boolean enableEmailCheck) {
      this.enableEmailCheck = enableEmailCheck;
      return this;
   }

   public boolean enableUrlCheck() {
      return this.enableUrlCheck;
   }

   public SensitiveWordContext enableUrlCheck(boolean enableUrlCheck) {
      this.enableUrlCheck = enableUrlCheck;
      return this;
   }

   public int sensitiveCheckNumLen() {
      return this.sensitiveCheckNumLen;
   }

   public SensitiveWordContext sensitiveCheckNumLen(int sensitiveCheckNumLen) {
      this.sensitiveCheckNumLen = sensitiveCheckNumLen;
      return this;
   }

   public IWordFormat wordFormat() {
      return this.wordFormat;
   }

   public SensitiveWordContext wordFormat(IWordFormat wordFormat) {
      this.wordFormat = wordFormat;
      return this;
   }

   public IWordTag wordTag() {
      return this.wordTag;
   }

   public SensitiveWordContext wordTag(IWordTag wordTag) {
      this.wordTag = wordTag;
      return this;
   }

   public ISensitiveWordCharIgnore charIgnore() {
      return this.charIgnore;
   }

   public SensitiveWordContext charIgnore(ISensitiveWordCharIgnore charIgnore) {
      this.charIgnore = charIgnore;
      return this;
   }

   public IWordResultCondition wordResultCondition() {
      return this.wordResultCondition;
   }

   public SensitiveWordContext wordResultCondition(IWordResultCondition wordResultCondition) {
      this.wordResultCondition = wordResultCondition;
      return this;
   }

   public IWordCheck wordCheckWord() {
      return this.wordCheckWord;
   }

   public SensitiveWordContext wordCheckWord(IWordCheck wordCheckWord) {
      this.wordCheckWord = wordCheckWord;
      return this;
   }

   public IWordCheck wordCheckNum() {
      return this.wordCheckNum;
   }

   public SensitiveWordContext wordCheckNum(IWordCheck wordCheckNum) {
      this.wordCheckNum = wordCheckNum;
      return this;
   }

   public IWordCheck wordCheckEmail() {
      return this.wordCheckEmail;
   }

   public SensitiveWordContext wordCheckEmail(IWordCheck wordCheckEmail) {
      this.wordCheckEmail = wordCheckEmail;
      return this;
   }

   public IWordCheck wordCheckUrl() {
      return this.wordCheckUrl;
   }

   public SensitiveWordContext wordCheckUrl(IWordCheck wordCheckUrl) {
      this.wordCheckUrl = wordCheckUrl;
      return this;
   }

   public IWordCheck wordCheckIpv4() {
      return this.wordCheckIpv4;
   }

   public SensitiveWordContext wordCheckIpv4(IWordCheck wordCheckIpv4) {
      this.wordCheckIpv4 = wordCheckIpv4;
      return this;
   }
}
