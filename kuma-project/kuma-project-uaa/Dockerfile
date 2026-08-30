# ============================================================
# UAA Spring Boot — Production Image
# Base: eclipse-temurin:25-jdk
# Port: 33336
# ============================================================
FROM eclipse-temurin:25-jdk

RUN mkdir -p /app /app/data /data/uaa/logs \
    && groupadd -r uaa \
    && useradd -r -g uaa -d /app -s /sbin/nologin uaa \
    && chown -R uaa:uaa /app /data/uaa/logs

WORKDIR /app

ARG JAR_VERSION
COPY uaa-${JAR_VERSION}.jar app.jar
RUN chown uaa:uaa app.jar

USER uaa

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV SPRING_PROFILES_ACTIVE=prod
ENV UAA_JWK_PRIVATE_KEY=/app/data/uaa-jwt-rsa-private.pem

EXPOSE 33336

ENTRYPOINT ["sh", "-c", "exec java --enable-preview -XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF-8 \"-Dspring.profiles.active=$SPRING_PROFILES_ACTIVE\" \"-Dkuma.uaa.jwk.private-key-location=$UAA_JWK_PRIVATE_KEY\" -jar app.jar"]
