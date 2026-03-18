package com.kuma.boot.grpc.utils;

import com.kuma.boot.common.utils.log.LogUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;

public class ManagedChannelUtils {
   private static ManagedChannel initClientServe(String url, String port) {
      return ManagedChannelBuilder.forAddress(url, Integer.parseInt(port)).usePlaintext().enableRetry().maxRetryAttempts(5).keepAliveTime(5L, TimeUnit.SECONDS).build();
   }

   public static void runWithManagedChannel(String url, String port, ChannelConsumer consumer) {
      try (ManagedChannelWrapper channelWrapper = new ManagedChannelWrapper(initClientServe(url, port))) {
         ManagedChannel channel = channelWrapper.getChannel();
         consumer.accept(channel);
      } catch (Exception e) {
         LogUtils.error(e);
      }

   }

   public static class ManagedChannelWrapper implements AutoCloseable {
      private final ManagedChannel channel;

      public ManagedChannelWrapper(ManagedChannel channel) {
         this.channel = channel;
      }

      public void close() {
         if (this.channel != null && !this.channel.isShutdown()) {
            this.channel.shutdown();
         }

      }

      public ManagedChannel getChannel() {
         return this.channel;
      }
   }

   @FunctionalInterface
   public interface ChannelConsumer {
      void accept(ManagedChannel channel) throws Exception;
   }
}
