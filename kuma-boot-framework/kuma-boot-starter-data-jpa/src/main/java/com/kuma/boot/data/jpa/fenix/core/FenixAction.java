package com.kuma.boot.data.jpa.fenix.core;

import java.util.Map;

@FunctionalInterface
public interface FenixAction {
   void execute(final StringBuilder join, final Map<String, Object> params);
}
