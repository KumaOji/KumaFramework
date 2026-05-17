# ============================================================
# Blog Spring Boot — Production Image
# Base: eclipse-temurin:25-jdk (本地自建 JDK 镜像，服务器执行一次构建后永久缓存)
# Port: 9000
# ============================================================
FROM eclipse-temurin:25-jdk

# ---- runtime directories ----
RUN mkdir -p /app /data/music /data/blog/logs \
    && groupadd -r blog \
    && useradd -r -g blog -d /app -s /sbin/nologin blog \
    && chown -R blog:blog /app /data/music /data/blog/logs

WORKDIR /app

# ---- copy fat jar (place blog.jar next to Dockerfile before building) ----
COPY blog-2026.05.01.jar app.jar
RUN chown blog:blog app.jar

# ---- runtime user (non-root) ----
USER blog

# ---- locale / charset ----
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# ---- Spring Boot profile ----
ENV SPRING_PROFILES_ACTIVE=prod

# ---- required env vars (must be supplied at docker run / compose) ----
# ADMIN_INITIAL_PASSWORD
# MYSQL_URL  MYSQL_USERNAME  MYSQL_PASSWORD
# REDIS_HOST  REDIS_PORT  REDIS_DATABASE
# (REDIS_JEDIS_POOL_MAX_ACTIVE / MAX_IDLE / MIN_IDLE — optional, defaults to 8/8/0)

EXPOSE 9000

# exec form: sh -c expands $VAR, exec replaces sh so Java receives OS signals directly
ENTRYPOINT ["sh", "-c", "exec java --enable-preview -XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF-8 \"-Dspring.profiles.active=$SPRING_PROFILES_ACTIVE\" \"-Dspring.datasource.dynamic.datasource.mysql.url=$MYSQL_URL\" \"-Dspring.datasource.dynamic.datasource.mysql.username=$MYSQL_USERNAME\" \"-Dspring.datasource.dynamic.datasource.mysql.password=$MYSQL_PASSWORD\" \"-Dspring.data.redis.host=$REDIS_HOST\" \"-Dspring.data.redis.port=$REDIS_PORT\" \"-Dspring.data.redis.database=$REDIS_DATABASE\" -jar app.jar"]
