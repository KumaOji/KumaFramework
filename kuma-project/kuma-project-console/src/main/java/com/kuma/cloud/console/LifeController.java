package com.kuma.cloud.console;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/life")
public class LifeController {
    private final LifeLifecycle life;
    public LifeController(LifeLifecycle life) { this.life = life; }
    @GetMapping public Object status() { return life.status(); }
    @GetMapping("/details") public Object details() { return life.details(); }
    @PostMapping("/{action}") public Object action(@PathVariable String action, HttpServletRequest request) {
        AiController.checkOrigin(request); return life.action(action);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object invalid(IllegalArgumentException e) { return Map.of("error", e.getMessage()); }
}
