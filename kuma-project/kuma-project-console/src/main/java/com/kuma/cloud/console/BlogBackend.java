package com.kuma.cloud.console;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class BlogBackend extends LocalJavaService {
    public BlogBackend(@Value("${console.workspace:}") String workspace,
                       @Value("${console.blog.backend-distribution:Ubuntu-24.04}") String distribution,
                       CommandRunner commands, ObjectMapper mapper) {
        super(workspace, distribution, commands, mapper, "blog");
    }
}
