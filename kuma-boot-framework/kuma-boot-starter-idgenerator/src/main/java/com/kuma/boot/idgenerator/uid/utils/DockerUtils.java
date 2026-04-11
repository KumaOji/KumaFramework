package com.kuma.boot.idgenerator.uid.utils;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;

public abstract class DockerUtils {
   private static final String ENV_KEY_HOST = "JPAAS_HOST";
   private static final String ENV_KEY_PORT = "JPAAS_HTTP_PORT";
   private static final String ENV_KEY_PORT_ORIGINAL = "JPAAS_HOST_PORT_8080";
   private static String DOCKER_HOST = "";
   private static String DOCKER_PORT = "";
   private static boolean IS_DOCKER;

   public DockerUtils() {
   }

   public static String getDockerHost() {
      return DOCKER_HOST;
   }

   public static String getDockerPort() {
      return DOCKER_PORT;
   }

   public static boolean isDocker() {
      return IS_DOCKER;
   }

   private static void retrieveFromEnv() {
      DOCKER_HOST = System.getenv("JPAAS_HOST");
      DOCKER_PORT = System.getenv("JPAAS_HTTP_PORT");
      if (StringUtils.isBlank(DOCKER_PORT)) {
         DOCKER_PORT = System.getenv("JPAAS_HOST_PORT_8080");
      }

      boolean hasEnvHost = StringUtils.isNotBlank(DOCKER_HOST);
      boolean hasEnvPort = StringUtils.isNotBlank(DOCKER_PORT);
      if (hasEnvHost && hasEnvPort) {
         IS_DOCKER = true;
      } else {
         if (hasEnvHost || hasEnvPort) {
            LogUtils.error("[uid-gen] Missing host or port from env for Docker. host:{}, port:{}", new Object[]{DOCKER_HOST, DOCKER_PORT});
            throw new RuntimeException("Missing host or port from env for Docker. host:" + DOCKER_HOST + ", port:" + DOCKER_PORT);
         }

         IS_DOCKER = false;
      }

   }

   static {
      retrieveFromEnv();
   }
}
