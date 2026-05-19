package com.kuma.cloud.blog.service.impl;

import com.kuma.cloud.blog.domain.vo.OpenWebUiChatRequest;
import com.kuma.cloud.blog.service.OpenWebUiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executor;

@Service
public class OpenWebUiServiceImpl implements OpenWebUiService {

    private static final Logger logger = LoggerFactory.getLogger(OpenWebUiServiceImpl.class);

    private final RestClient restClient;
    private final String apiKey;
    private final Executor asyncExecutor;

    public OpenWebUiServiceImpl(
            @Value("${openwebui.base-url:http://blog-ai-ui:8080}") String baseUrl,
            @Value("${openwebui.api-key:}") String apiKey,
            @Qualifier("asyncThreadPoolTaskExecutor") Executor asyncExecutor) {
        this.apiKey = apiKey;
        this.asyncExecutor = asyncExecutor;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> listModels() {
        return restClient.get()
                .uri("/api/models")
                .retrieve()
                .body(Map.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> chat(OpenWebUiChatRequest request) {
        request.setStream(false);
        return restClient.post()
                .uri("/api/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);
    }

    @Override
    public SseEmitter streamChat(OpenWebUiChatRequest request) {
        request.setStream(true);
        SseEmitter emitter = new SseEmitter(600_000L);

        asyncExecutor.execute(() -> {
            try {
                restClient.post()
                        .uri("/api/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .exchange((req, resp) -> {
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(resp.getBody(), StandardCharsets.UTF_8))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (!line.startsWith("data: ")) continue;
                                    String data = line.substring(6).trim();
                                    if ("[DONE]".equals(data)) break;
                                    emitter.send(SseEmitter.event().data(data));
                                }
                                emitter.complete();
                            } catch (Exception e) {
                                logger.warn("流式推送中断: {}", e.getMessage());
                                emitter.completeWithError(e);
                            }
                            return null;
                        });
            } catch (Exception e) {
                logger.error("OpenWebUI 流式对话异常: {}", e.getMessage(), e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
