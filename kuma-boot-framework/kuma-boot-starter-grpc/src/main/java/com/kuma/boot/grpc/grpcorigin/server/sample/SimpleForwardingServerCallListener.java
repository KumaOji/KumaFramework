package com.kuma.boot.grpc.grpcorigin.server.sample;

import io.grpc.ForwardingServerCallListener;
import io.grpc.ServerCall;

public class SimpleForwardingServerCallListener extends ForwardingServerCallListener.SimpleForwardingServerCallListener<Object> {
   @SuppressWarnings("unchecked")
   protected SimpleForwardingServerCallListener(ServerCall.Listener delegate) {
      super(delegate);
   }

   public void onMessage(Object message) {
      this.onMessageBefore(message);
      super.onMessage(message);
      this.onMessageAfter(message);
   }

   public void onHalfClose() {
      this.onHalfCloseBefore();
      super.onHalfClose();
      this.onHalfCloseAfter();
   }

   public void onCancel() {
      this.onCancelBefore();
      super.onCancel();
      this.onCancelAfter();
   }

   public void onComplete() {
      this.onCompleteBefore();
      super.onComplete();
      this.onCompleteAfter();
   }

   public void onReady() {
      this.onReadyBefore();
      super.onReady();
      this.onReadyAfter();
   }

   public void onMessageBefore(Object message) {
   }

   public void onMessageAfter(Object message) {
   }

   public void onHalfCloseBefore() {
   }

   public void onHalfCloseAfter() {
   }

   public void onCancelBefore() {
   }

   public void onCancelAfter() {
   }

   public void onCompleteBefore() {
   }

   public void onCompleteAfter() {
   }

   public void onReadyBefore() {
   }

   public void onReadyAfter() {
   }
}
