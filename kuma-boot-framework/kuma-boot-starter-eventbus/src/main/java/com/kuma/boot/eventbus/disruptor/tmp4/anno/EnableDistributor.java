package com.kuma.boot.eventbus.disruptor.tmp4.anno;

import com.kuma.boot.eventbus.disruptor.tmp4.autoconfigure.DistributorAutoConfigure;
import org.springframework.context.annotation.Import;

@Import({DistributorAutoConfigure.class})
public @interface EnableDistributor {
}
