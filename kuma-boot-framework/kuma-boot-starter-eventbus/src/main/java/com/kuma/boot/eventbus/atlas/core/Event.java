package com.kuma.boot.eventbus.atlas.core;

import java.util.Map;

public interface Event {
   String getId();

   String getType();

   Object getData();

   long getTimestamp();

   Map<String, Object> getMetadata();
}
