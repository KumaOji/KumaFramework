/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.ntp;

import com.kuma.boot.common.utils.log.LogUtils;
import java.net.InetAddress;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 * ntp 校时服务
 */
public class Ntp {

    private final long diff;

    /**
     * 计算时差
     */
    public static long diff(String host) {
        try {
            NTPUDPClient client = new NTPUDPClient();
            TimeInfo time = client.getTime(InetAddress.getByName(host));
            long systemMillis = System.currentTimeMillis();
            long ntpMillis = time.getMessage().getTransmitTimeStamp().getTime();
            return ntpMillis - systemMillis;
        } catch (Exception e) {
            throw new NtpException("ntp初始化异常!", e);
        }
    }

    public Ntp(String host) {
        try {
            diff = diff(host);
            LogUtils.warn("授时中心时间与系统时间差为 {} 毫秒", diff);
        } catch (Exception e) {
            throw new NtpException("ntp初始化异常!", e);
        }
    }

    public long currentMillis() {
        return System.currentTimeMillis() + diff;
    }

    /**
     * 切换授时中心
     * @return 返回新的ntp实例
     */
    public Ntp use(String host) {
        return new Ntp(host);
    }
}
