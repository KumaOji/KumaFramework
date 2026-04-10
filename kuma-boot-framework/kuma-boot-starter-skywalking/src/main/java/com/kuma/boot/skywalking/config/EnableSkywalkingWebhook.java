package com.kuma.boot.skywalking.config;

import com.kuma.boot.skywalking.alarm.SkywalkingCallbackController;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({SkywalkingCallbackController.class})
public @interface EnableSkywalkingWebhook {
}
