package com.kuma.boot.core.support;

import com.kuma.boot.core.enums.StatusEnum;

public interface StatusListener {

    void onApplicationEvent(StatusEnum event);
}
