package com.kuma.boot.idgenerator.uid.buffer;

import java.util.List;

@FunctionalInterface
public interface BufferedUidProvider {
   List<Long> provide(long momentInSecond);
}
