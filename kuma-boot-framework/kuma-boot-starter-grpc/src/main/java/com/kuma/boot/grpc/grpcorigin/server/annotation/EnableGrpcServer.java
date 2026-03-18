package com.kuma.boot.grpc.grpcorigin.server.annotation;

import com.kuma.boot.grpc.grpcorigin.server.configuration.GrpcServerAutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({GrpcServerAutoConfiguration.class})
public @interface EnableGrpcServer {
}
