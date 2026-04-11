package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DefaultNamedHandlerList implements NamedHandlerList<DisruptorEvent> {
   private String name;
   private List<DisruptorHandler<DisruptorEvent>> backingList;

   public DefaultNamedHandlerList(String name) {
      this(name, new ArrayList());
   }

   public DefaultNamedHandlerList(String name, List<DisruptorHandler<DisruptorEvent>> backingList) {
      if (backingList == null) {
         throw new NullPointerException("backingList constructor argument cannot be null.");
      } else {
         this.backingList = backingList;
         this.setName(name);
      }
   }

   public void setName(String name) {
      if (StringUtils.isBlank(name)) {
         throw new IllegalArgumentException("Cannot specify a null or empty name.");
      } else {
         this.name = name;
      }
   }

   public String getName() {
      return this.name;
   }

   public HandlerChain<DisruptorEvent> proxy(HandlerChain<DisruptorEvent> handlerChain) {
      return new ProxiedHandlerChain((ProxiedHandlerChain)handlerChain, this);
   }

   public int size() {
      return this.backingList.size();
   }

   public boolean isEmpty() {
      return this.backingList.isEmpty();
   }

   public boolean contains(Object o) {
      return this.backingList.contains(o);
   }

   public Iterator<DisruptorHandler<DisruptorEvent>> iterator() {
      return this.backingList.iterator();
   }

   public Object[] toArray() {
      return this.backingList.toArray();
   }

   public <T> T[] toArray(T[] a) {
      return (T[])this.backingList.toArray(a);
   }

   public boolean add(DisruptorHandler<DisruptorEvent> e) {
      return this.backingList.add(e);
   }

   public boolean remove(Object o) {
      return this.backingList.remove(o);
   }

   public boolean containsAll(Collection<?> c) {
      return this.backingList.containsAll(c);
   }

   public boolean addAll(Collection<? extends DisruptorHandler<DisruptorEvent>> c) {
      return this.backingList.addAll(c);
   }

   public boolean addAll(int index, Collection<? extends DisruptorHandler<DisruptorEvent>> c) {
      return this.backingList.addAll(index, c);
   }

   public boolean removeAll(Collection<?> c) {
      return this.backingList.removeAll(c);
   }

   public boolean retainAll(Collection<?> c) {
      return this.backingList.retainAll(c);
   }

   public void clear() {
      this.backingList.clear();
   }

   public DisruptorHandler<DisruptorEvent> get(int index) {
      return (DisruptorHandler)this.backingList.get(index);
   }

   public DisruptorHandler<DisruptorEvent> set(int index, DisruptorHandler<DisruptorEvent> element) {
      return (DisruptorHandler)this.backingList.set(index, element);
   }

   public void add(int index, DisruptorHandler<DisruptorEvent> element) {
      this.backingList.add(index, element);
   }

   public DisruptorHandler<DisruptorEvent> remove(int index) {
      return (DisruptorHandler)this.backingList.remove(index);
   }

   public int indexOf(Object o) {
      return this.backingList.indexOf(o);
   }

   public int lastIndexOf(Object o) {
      return this.backingList.lastIndexOf(o);
   }

   public ListIterator<DisruptorHandler<DisruptorEvent>> listIterator() {
      return this.backingList.listIterator();
   }

   public ListIterator<DisruptorHandler<DisruptorEvent>> listIterator(int index) {
      return this.backingList.listIterator(index);
   }

   public List<DisruptorHandler<DisruptorEvent>> subList(int fromIndex, int toIndex) {
      return this.backingList.subList(fromIndex, toIndex);
   }
}
