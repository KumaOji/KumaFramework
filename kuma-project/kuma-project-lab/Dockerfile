FROM eclipse-temurin:25-jdk
RUN mkdir -p /app && useradd -r -d /app -s /sbin/nologin lab && chown lab:lab /app
WORKDIR /app
ARG JAR_VERSION
COPY --chown=lab:lab lab-${JAR_VERSION}.jar app.jar
USER lab
ENV LANG=C.UTF-8
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 19090
ENTRYPOINT ["java", "--enable-preview", "--enable-native-access=ALL-UNNAMED", "-XX:MaxRAMPercentage=70.0", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
