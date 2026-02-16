package cn.kuma.blog.main.service;

import java.util.List;
import java.util.Map;

/**
 * 翻译服务接口
 *
 * @author Kuma
 * @version 1.0
 */
public interface TranslateService {

    /**
     * 翻译文本
     *
     * @param text 要翻译的文本
     * @param source 源语言代码
     * @param target 目标语言代码
     * @return 翻译结果
     */
    Map<String, Object> translate(String text, String source, String target);

    /**
     * 获取支持的语言列表
     *
     * @return 语言列表
     */
    List<Map<String, Object>> getLanguages();

    /**
     * 检测文本语言
     *
     * @param text 要检测的文本
     * @return 检测结果
     */
    List<Map<String, Object>> detectLanguage(String text);

    /**
     * 检查翻译服务是否可用
     *
     * @return 是否可用
     */
    boolean isServiceAvailable();
}
