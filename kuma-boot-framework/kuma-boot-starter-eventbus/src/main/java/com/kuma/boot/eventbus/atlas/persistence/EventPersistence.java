package com.kuma.boot.eventbus.atlas.persistence;

import com.kuma.boot.eventbus.atlas.core.Event;
import java.util.List;

public interface EventPersistence {
   void save(Event event);

   List<Event> findByType(String eventType, int limit);

   Event findById(String id);
}
