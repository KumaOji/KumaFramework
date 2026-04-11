package com.kuma.boot.eventbus.atlas.persistence;

import com.kuma.boot.eventbus.atlas.core.Event;
import java.util.List;

public class DatabaseEventPersistence implements EventPersistence {
   public DatabaseEventPersistence() {
   }

   public void save(Event event) {
      System.out.println("Saving event to database: " + event.getId());
   }

   public List<Event> findByType(String eventType, int limit) {
      System.out.println("Finding events by type: " + eventType);
      return null;
   }

   public Event findById(String id) {
      System.out.println("Finding event by id: " + id);
      return null;
   }
}
