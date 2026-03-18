package com.kuma.boot.grpc.grpcorigin.client.sample;

import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;

public class SimpleForwardingClientCall<ReqT, RespT> extends ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT> {
   protected SimpleForwardingClientCall(ClientCall<ReqT, RespT> delegate) {
      super(delegate);
   }

   public void start(ClientCall.Listener<RespT> responseListener, Metadata headers) {
      this.onStartBefore(responseListener, headers);
      super.start(responseListener, headers);
      this.onStartAfter(responseListener, headers);
   }

   public void sendMessage(ReqT message) {
      this.onSendMessageBefore(message);
      super.sendMessage(message);
      this.onSendMessageAfter(message);
   }

   public void halfClose() {
      this.onHalfCloseBefore();
      super.halfClose();
      this.onHalfCloseAfter();
   }

   public void onStartBefore(ClientCall.Listener<RespT> responseListener, Metadata headers) {
   }

   public void onStartAfter(ClientCall.Listener<RespT> responseListener, Metadata headers) {
   }

   public void onSendMessageBefore(ReqT message) {
   }

   public void onSendMessageAfter(ReqT message) {
   }

   public void onHalfCloseBefore() {
   }

   public void onHalfCloseAfter() {
   }
}
