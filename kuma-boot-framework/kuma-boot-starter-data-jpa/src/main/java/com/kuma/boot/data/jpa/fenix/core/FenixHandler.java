package com.kuma.boot.data.jpa.fenix.core;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;

@FunctionalInterface
public interface FenixHandler {
   void buildSqlInfo(BuildSource source);
}
