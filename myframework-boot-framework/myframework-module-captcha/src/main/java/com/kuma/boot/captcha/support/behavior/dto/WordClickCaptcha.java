/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.kuma.boot.captcha.support.behavior.dto;

import com.google.common.base.MoreObjects;
import com.kuma.boot.captcha.support.core.dto.Captcha;

public class WordClickCaptcha
extends Captcha {
    private String wordClickImageBase64;
    private String words;
    private Integer wordsCount;

    public String getWordClickImageBase64() {
        return this.wordClickImageBase64;
    }

    public void setWordClickImageBase64(String wordClickImageBase64) {
        this.wordClickImageBase64 = wordClickImageBase64;
    }

    public String getWords() {
        return this.words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public Integer getWordsCount() {
        return this.wordsCount;
    }

    public void setWordsCount(Integer wordsCount) {
        this.wordsCount = wordsCount;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("wordClickImageBase64", (Object)this.wordClickImageBase64).add("words", (Object)this.words).add("wordsCount", (Object)this.wordsCount).toString();
    }
}

