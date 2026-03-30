# ============================================================
# Blog Spring Boot — Production Image
# Base: temurin:25 (本地自建 JDK 镜像，服务器执行一次构建后永久缓存)
# Port: 9000
# ============================================================
FROM temurin:25

# ---- runtime directories ----
RUN mkdir -p /app /data/music /data/blog/logs \
    && groupadd -r blog \
    && useradd -r -g blog -d /app -s /sbin/nologin blog \
    && chown -R blog:blog /app /data/music /data/blog/logs

WORKDIR /app

# ---- copy fat jar (place blog.jar next to Dockerfile before building) ----
COPY blog-2026.03.jar app.jar
RUN chown blog:blog app.jar

# ---- runtime user (non-root) ----
USER blog

# ---- Spring Boot profile ----
ENV SPRING_PROFILES_ACTIVE=prod

# ---- required env vars (must be supplied at docker run / compose) ----
# ADMIN_INITIAL_PASSWORD
# MYSQL_URL  MYSQL_USERNAME  MYSQL_PASSWORD
# REDIS_HOST  REDIS_PORT  REDIS_DATABASE
# (REDIS_JEDIS_POOL_MAX_ACTIVE / MAX_IDLE / MIN_IDLE — optional, defaults to 8/8/0)

EXPOSE 9000

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:9000/api/actuator/health || exit 1

# shell form — allows $VAR substitution so env vars override baked-in yml values
ENTRYPOINT java --enable-preview \
    -XX:+UseG1GC \
    -XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.datasource.dynamic.datasource.mysql.url="$MYSQL_URL" \
    -Dspring.datasource.dynamic.datasource.mysql.username="$MYSQL_USERNAME" \
    -Dspring.datasource.dynamic.datasource.mysql.password="$MYSQL_PASSWORD" \
    -Dspring.data.redis.host="$REDIS_HOST" \
    -Dspring.data.redis.port="$REDIS_PORT" \
    -Dspring.data.redis.database="$REDIS_DATABASE" \
    -jar app.jar
