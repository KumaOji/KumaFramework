/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.lang.StringUtils;

/** 表情工具类 */
public final class EmojiUtils {

    private EmojiUtils() {}

    /**
     * 替换掉 emoji 表情
     * @param text 文本
     * @param replaceText 替换的文本
     * @return 结果
     */
    public static String replaceEmoji(final String text, final String replaceText) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        return text.replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", replaceText);
    }

    /**
     * 替换掉 emoji 表情
     * @param text 文本
     * @return 结果
     */
    public static String replaceEmoji(final String text) {
        return replaceEmoji(text, "");
    }
}
