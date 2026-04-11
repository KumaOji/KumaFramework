package com.kuma.boot.sensitive.sensitiveword.api;

import com.kuma.boot.sensitive.sensitiveword.bs.SensitiveWordContext;

public interface IWordContext {
   boolean wordFailFast();

   IWordContext wordFailFast(boolean wordFailFast);

   boolean ignoreCase();

   boolean ignoreWidth();

   boolean ignoreNumStyle();

   IWordContext ignoreCase(boolean ignoreCase);

   IWordContext ignoreWidth(boolean ignoreWidth);

   IWordContext ignoreNumStyle(boolean ignoreNumStyle);

   boolean ignoreChineseStyle();

   IWordContext ignoreChineseStyle(final boolean ignoreChineseStyle);

   boolean enableWordCheck();

   IWordContext enableWordCheck(boolean enableWordCheck);

   boolean enableNumCheck();

   IWordContext enableNumCheck(final boolean enableNumCheck);

   boolean enableEmailCheck();

   IWordContext enableEmailCheck(final boolean enableEmailCheck);

   boolean enableUrlCheck();

   IWordContext enableUrlCheck(final boolean enableUrlCheck);

   boolean enableIpv4Check();

   IWordContext enableIpv4Check(final boolean enableIpv4Check);

   boolean ignoreEnglishStyle();

   IWordContext ignoreEnglishStyle(final boolean ignoreEnglishStyle);

   boolean ignoreRepeat();

   IWordContext ignoreRepeat(final boolean ignoreRepeat);

   int sensitiveCheckNumLen();

   IWordContext sensitiveCheckNumLen(final int sensitiveCheckNumLen);

   IWordContext sensitiveCheck(final IWordCheck sensitiveCheck);

   IWordCheck sensitiveCheck();

   IWordContext wordReplace(final IWordReplace wordReplace);

   IWordReplace wordReplace();

   IWordContext wordFormat(final IWordFormat wordFormat);

   IWordFormat wordFormat();

   IWordData wordData();

   IWordContext wordData(IWordData wordMap);

   IWordData wordDataAllow();

   IWordContext wordDataAllow(IWordData wordDataAllow);

   IWordTag wordTag();

   SensitiveWordContext wordTag(IWordTag wordTag);

   ISensitiveWordCharIgnore charIgnore();

   SensitiveWordContext charIgnore(ISensitiveWordCharIgnore charIgnore);

   IWordResultCondition wordResultCondition();

   SensitiveWordContext wordResultCondition(IWordResultCondition wordResultCondition);

   IWordCheck wordCheckWord();

   SensitiveWordContext wordCheckWord(IWordCheck wordCheckWord);

   IWordCheck wordCheckNum();

   SensitiveWordContext wordCheckNum(IWordCheck wordCheckNum);

   IWordCheck wordCheckEmail();

   SensitiveWordContext wordCheckEmail(IWordCheck wordCheckEmail);

   IWordCheck wordCheckUrl();

   SensitiveWordContext wordCheckUrl(IWordCheck wordCheckUrl);

   IWordCheck wordCheckIpv4();

   SensitiveWordContext wordCheckIpv4(IWordCheck wordCheckIpv4);
}
