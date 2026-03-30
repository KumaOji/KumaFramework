# ============================================================
# Blog Spring Boot — Production Image
# Base: ubuntu:22.04 (already pulled on server)
# JDK: Temurin 25 (required by the project — uses --enable-preview)
# Port: 9000
# ============================================================
FROM ubuntu:22.04

# ---- install Temurin 25 JDK ----
RUN apt-get update -qq \
    && apt-get install -y --no-install-recommends \
        wget gnupg ca-certificates \
    && wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public \
       | gpg --dearmor -o /usr/share/keyrings/adoptium.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/adoptium.gpg] \
       https://packages.adoptium.net/artifactory/deb jammy main" \
       > /etc/apt/sources.list.d/adoptium.list \
    && apt-get update -qq \
    && apt-get install -y --no-install-recommends temurin-25-jdk \
    && apt-get purge -y wget gnupg \
    && apt-get autoremove -y \
    && rm -rf /var/lib/apt/lists/*

# ---- runtime directories ----
RUN mkdir -p /app /data/music /data/blog/logs \
    && groupadd -r blog \
    && useradd -r -g blog -d /app -s /sbin/nologin blog \
    && chown -R blog:blog /app /data/music /data/blog/logs

WORKDIR /app

# ---- copy fat jar ----
COPY build/libs/blog.jar app.jar
RUN chown blog:blog app.jar

# ---- runtime user (non-root) ----
USER blog

# ---- Spring Boot profile ----
ENV SPRING_PROFILES_ACTIVE=prod

# ---- required env vars (must be supplied at docker run / compose) ----
# ADMIN_INITIAL_PASSWORD
# mysql_url  mysql_username  mysql_password
# redis_host  redis_port  redis_database
# redis_jedis_pool_max_active  redis_jedis_pool_max_idle  redis_jedis_pool_min_idle

EXPOSE 9000

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:9000/api/actuator/health || exit 1

ENTRYPOINT ["java", \
    "--enable-preview", \
    "-XX:+UseG1GC", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
