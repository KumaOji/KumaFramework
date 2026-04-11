package com.kuma.boot.eventbus.atlas.core;

public interface OrderedEvent extends Event {
   String getOrderKey();
}
