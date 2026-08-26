package com.kuma.cloud.lab.spring.ioc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 演示构造器注入：默认注入 @Primary Bean，@Qualifier 可指定具体实现。
 */
@Service
public class MessageService {

    private final MessageRenderer primaryRenderer;
    private final MessageRenderer plainRenderer;

    public MessageService(
            MessageRenderer primaryRenderer,
            @Qualifier("plainMessageRenderer") MessageRenderer plainRenderer) {
        this.primaryRenderer = primaryRenderer;
        this.plainRenderer = plainRenderer;
    }

    public String renderWithPrimary(String message) {
        return primaryRenderer.render(message);
    }

    public String renderWithPlain(String message) {
        return plainRenderer.render(message);
    }

    public String primaryRendererType() {
        return primaryRenderer.type();
    }

    public String plainRendererType() {
        return plainRenderer.type();
    }

}
