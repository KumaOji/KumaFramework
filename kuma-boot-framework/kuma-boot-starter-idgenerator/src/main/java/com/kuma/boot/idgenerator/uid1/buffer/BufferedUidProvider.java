package com.kuma.boot.idgenerator.uid1.buffer;

import java.util.List;

@FunctionalInterface
public interface BufferedUidProvider {
   List<Long> provide(long momentInSecond);
}
