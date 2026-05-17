package com.kuma.boot.common.support;

import com.kuma.boot.common.enums.StatusEnum;

public interface StatusListener {

    void onApplicationEvent(StatusEnum event);
}
