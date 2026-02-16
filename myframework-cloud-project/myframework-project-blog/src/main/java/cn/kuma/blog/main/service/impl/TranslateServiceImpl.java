package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.main.service.TranslateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 翻译服务实现类
 * 代理转发请求到内部的 LibreTranslate 服务
 *
 * @author Kuma
 * @version 1.0
 */
@Service
public class TranslateServiceImpl implements TranslateService {

    private static final Logger logger = LoggerFactory.getLogger(TranslateServiceImpl.class);

    /**
     * LibreTranslate 服务地址（内部地址，只能本机访问）
     */
    @Value("${translate.libre-translate-url:http://127.0.0.1:5000}")
    private String libreTranslateUrl;

    /**
     * 请求超时时间（毫秒）
     */
    @Value("${translate.timeout:30000}")
    private int timeout;

    private final RestTemplate restTemplate;

    public TranslateServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Map<String, Object> translate(String text, String source, String target) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("q", text);
            requestBody.put("source", source);
            requestBody.put("target", target);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    libreTranslateUrl + "/translate",
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
            throw new RuntimeException("翻译服务返回错误: " + response.getStatusCode());
        } catch (Exception e) {
            logger.error("翻译失败: {}", e.getMessage());
            throw new RuntimeException("翻译失败: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getLanguages() {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(
                    libreTranslateUrl + "/languages",
                    List.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
            throw new RuntimeException("获取语言列表失败: " + response.getStatusCode());
        } catch (Exception e) {
            logger.error("获取语言列表失败: {}", e.getMessage());
            throw new RuntimeException("获取语言列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> detectLanguage(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("q", text);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<List> response = restTemplate.postForEntity(
                    libreTranslateUrl + "/detect",
                    entity,
                    List.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
            throw new RuntimeException("语言检测失败: " + response.getStatusCode());
        } catch (Exception e) {
            logger.error("语言检测失败: {}", e.getMessage());
            throw new RuntimeException("语言检测失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isServiceAvailable() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    libreTranslateUrl + "/languages",
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.warn("LibreTranslate 服务不可用: {}", e.getMessage());
            return false;
        }
    }
}
