/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.kuma.blog.framework.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtility {

    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";

    private static final Pattern UNDER_LINE_PATTERN = Pattern.compile("_(\\w)");

    public static String concat_ws(String separator, String... strs) {
        StringBuilder stringBuilder = new StringBuilder();
        if (separator == null) {
            separator = "";
        }
        for (String str : strs) {
            if (StringUtils.isNotBlank(str)) {
                stringBuilder.append(str).append(separator);
            }
        }
        String str = stringBuilder.toString();
        if (!separator.equals("")) {
            return str.substring(0, str.length() - 1);
        }
        else {
            return str;
        }
    }

    public static String snakeCase(String stringForm) {
        if (stringForm == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(stringForm.replace('.', '_'));
        for (int i = 1; i < builder.length() - 1; i++) {
            if (isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i), builder.charAt(i + 1))) {
                builder.insert(i++, '_');
            }
        }
        return builder.toString().toLowerCase(Locale.ENGLISH);
    }

    private static boolean isUnderscoreRequired(final char before, final char current, final char after) {
        return Character.isLowerCase(before) && Character.isUpperCase(current) && Character.isLowerCase(after);
    }

    public static String snakeCaseSameAsJacksonRule(String stringForm) {
        if (stringForm == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(stringForm.replace('.', '_'));
        for (int i = 1; i < builder.length() - 1; i++) {
            if (isUnderscoreRequiredSameAsJacksonRule(builder.charAt(i - 1), builder.charAt(i))) {
                builder.insert(i++, '_');
            }
        }
        return builder.toString().toLowerCase(Locale.ENGLISH);
    }

    private static boolean isUnderscoreRequiredSameAsJacksonRule(final char before, final char current) {
        return Character.isLowerCase(before) && Character.isUpperCase(current);
    }

    public static String camel(String word) {
        if (StringUtils.isBlank(word) || !StringUtils.contains(word, DEFAULT_QUALIFIER_DELIMITER)) {
            return word;
        }

        String[] split = StringUtils.split(word, DEFAULT_QUALIFIER_DELIMITER);
        StringBuilder sb = new StringBuilder(word.length());
        for (int i = 0; i < split.length; i++) {
            char[] chars = split[i].toCharArray();
            if (i != 0 && chars[0] >= 'a' && chars[0] <= 'z') {
                chars[0] -= 32;
            }
            sb.append(chars);
        }
        return sb.toString();
    }

    private String underLineToCamel(String source) {
        Matcher matcher = UNDER_LINE_PATTERN.matcher(source);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public static String capitalizeAllWords(String input) {
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                nextUpperCase = true;
                result.append(c);
            }
            else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                }
                else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

    public static String abbreviateMiddle(String str, int length) {
        return StringUtils.abbreviateMiddle(str, "...", length);
    }

}
