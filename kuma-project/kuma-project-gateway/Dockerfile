# ============================================================
# Gateway Spring Boot — Production Image
# Base: eclipse-temurin:25-jdk
# Port: 18080
# ============================================================
FROM eclipse-temurin:25-jdk

RUN mkdir -p /app /data/gateway/logs \
    && groupadd -r gateway \
    && useradd -r -g gateway -d /app -s /sbin/nologin gateway \
    && chown -R gateway:gateway /app /data/gateway/logs

WORKDIR /app

ARG JAR_VERSION
COPY gateway-${JAR_VERSION}.jar app.jar
RUN chown gateway:gateway app.jar

USER gateway

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 18080

ENTRYPOINT ["sh", "-c", "exec java --enable-preview -XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF-8 \"-Dspring.profiles.active=$SPRING_PROFILES_ACTIVE\" -jar app.jar"]
