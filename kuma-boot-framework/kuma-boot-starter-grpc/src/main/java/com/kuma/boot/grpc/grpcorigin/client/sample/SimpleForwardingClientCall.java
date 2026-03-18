package com.kuma.boot.grpc.grpcorigin.client.sample;

import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;

public class SimpleForwardingClientCall extends ForwardingClientCall.SimpleForwardingClientCall {
   protected SimpleForwardingClientCall(ClientCall delegate) {
      super(delegate);
   }

   public void start(ClientCall.Listener responseListener, Metadata headers) {
      this.onStartBefore(responseListener, headers);
      super.start(responseListener, headers);
      this.onStartAfter(responseListener, headers);
   }

   public void sendMessage(Object message) {
      this.onSendMessageBefore(message);
      super.sendMessage(message);
      this.onSendMessageAfter(message);
   }

   public void halfClose() {
      this.onHalfCloseBefore();
      super.halfClose();
      this.onHalfCloseAfter();
   }

   public void onStartBefore(ClientCall.Listener responseListener, Metadata headers) {
   }

   public void onStartAfter(ClientCall.Listener responseListener, Metadata headers) {
   }

   public void onSendMessageBefore(Object message) {
   }

   public void onSendMessageAfter(Object message) {
   }

   public void onHalfCloseBefore() {
   }

   public void onHalfCloseAfter() {
   }
}
