package com.kuma.blog.controller;

import com.kuma.blog.entity.Post;
import com.kuma.blog.mapper.PostMapper;
import com.kuma.cloud.utils.KumaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BlogController {

    private final PostMapper postMapper;

    @GetMapping("/hello")
    public Map<String, Object> hello(@RequestParam(required = false) String name) {
        String display = KumaUtils.defaultIfBlank(name, "Blog");
        String hash = KumaUtils.md5Hex(display);
        return Map.of(
            "message", "Hello, " + display,
            "md5", hash
        );
    }

    @GetMapping("/posts")
    public Map<String, Object> listByTitle(@RequestParam(required = false) String title) {
        String keyword = KumaUtils.defaultIfBlank(title, "");
        List<Post> list = postMapper.selectByTitleLike(keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("keyword", keyword);
        result.put("list", list);
        return result;
    }
}
