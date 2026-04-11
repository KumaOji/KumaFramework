package com.kuma.boot.canal.client;

public interface CanalClient {
   void start();

   void stop();

   boolean isRunning();
}
