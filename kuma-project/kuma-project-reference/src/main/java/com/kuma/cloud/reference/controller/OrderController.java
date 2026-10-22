package com.kuma.cloud.reference.controller;

import com.kuma.cloud.reference.domain.dto.CreateOrderDTO;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.cloud.reference.domain.qo.OrderPageQO;
import com.kuma.cloud.reference.domain.vo.OrderVO;
import com.kuma.boot.common.model.result.PageResultRecord;
import com.kuma.boot.web.annotation.BusinessApi;
import com.kuma.cloud.reference.service.OrderService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@BusinessApi
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final AsyncTaskExecutor executor;

    public OrderController(OrderService orderService,
            @Qualifier("asyncThreadPoolTaskExecutor") AsyncTaskExecutor executor) {
        this.orderService = orderService;
        this.executor = executor;
    }

    @PostMapping
    public ResponseEntity<OrderVO> create(@Valid @RequestBody CreateOrderDTO request,
            @RequestParam(defaultValue = "MAIN") DataScope scope) {
        var order = orderService.create(request, scope);
        return ResponseEntity.created(URI.create("/api/orders/" + order.id() + "?scope=" + scope)).body(order);
    }

    @GetMapping("/{id}")
    public CompletableFuture<OrderVO> get(@PathVariable Long id, @RequestParam(defaultValue = "MAIN") DataScope scope) {
        return executor.submitCompletable(() -> orderService.get(id, scope));
    }

    @GetMapping
    public CompletableFuture<PageResultRecord<OrderVO>> page(@Valid @ModelAttribute OrderPageQO query) {
        return executor.submitCompletable(() -> orderService.page(query));
    }

    @PostMapping("/{id}/confirm")
    public OrderVO confirm(@PathVariable Long id, @RequestParam(defaultValue = "MAIN") DataScope scope) {
        return orderService.confirm(id, scope);
    }
}
