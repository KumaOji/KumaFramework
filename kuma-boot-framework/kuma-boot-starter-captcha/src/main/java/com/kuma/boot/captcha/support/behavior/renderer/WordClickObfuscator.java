//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.behavior.renderer;

import cn.hutool.core.util.RandomUtil;
import com.kuma.boot.captcha.support.core.definition.domain.Coordinate;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordClickObfuscator {
    private final List<Coordinate> coordinates = new ArrayList();
    private final List<String> words = new ArrayList();
    private String wordString;

    public WordClickObfuscator(List<String> originalWords, List<Coordinate> originalCoordinates) {
        this.execute(originalWords, originalCoordinates);
    }

    private void execute(List<String> originalWords, List<Coordinate> originalCoordinates) {
        int[] indexes = RandomUtil.randomInts(originalWords.size());
        Arrays.stream(indexes).forEach((value) -> {
            this.words.add(this.words.size(), (String)originalWords.get(value));
            this.coordinates.add(this.coordinates.size(), (Coordinate)originalCoordinates.get(value));
        });
        this.wordString = StringUtils.join(this.getWords(), SymbolConstants.COMMA);
    }

    public List<Coordinate> getCoordinates() {
        return this.coordinates;
    }

    public List<String> getWords() {
        return this.words;
    }

    public String getWordString() {
        return this.wordString;
    }
}
