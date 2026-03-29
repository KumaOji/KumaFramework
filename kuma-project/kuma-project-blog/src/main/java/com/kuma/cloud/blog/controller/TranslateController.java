package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.security.BlogPermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * 翻译接口，代理请求到本地 LibreTranslate 服务
 *
 * @author Kuma
 */
@Tag(name = "翻译")
@RestController
@RequestMapping("/translate")
public class TranslateController {

    private final RestClient restClient;

    public TranslateController(@Value("${python.libreTranslate.port:5000}") int port) {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Operation(summary = "文本翻译（代理至 LibreTranslate）")
    @PostMapping
    @Authorize(BlogPermissions.TOOL_USE)
    public Result<Map<String, Object>> translate(
            @RequestParam("q") String q,
            @RequestParam(value = "source", defaultValue = "auto") String source,
            @RequestParam("target") String target) {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("q", q);
        form.add("source", source);
        form.add("target", target);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = restClient.post()
                .uri("/translate")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        return Result.success(result);
    }
}
