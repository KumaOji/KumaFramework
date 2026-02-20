/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 */
package com.kuma.boot.web.support.holidays.impl;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.support.holidays.config.HolidaysApiProperties;
import com.kuma.boot.web.support.holidays.core.DaysType;
import com.kuma.boot.web.support.holidays.core.HolidaysApi;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class HolidaysApiImpl
implements HolidaysApi,
InitializingBean {
    private static final Map<Integer, Map<String, Byte>> YEAR_DATA_MAP = new HashMap<Integer, Map<String, Byte>>();
    private final ResourceLoader resourceLoader;
    private final HolidaysApiProperties properties;

    public HolidaysApiImpl(ResourceLoader resourceLoader, HolidaysApiProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }

    @Override
    public DaysType getDaysType(LocalDate localDate) {
        int year = localDate.getYear();
        Map<String, Byte> dataMap = YEAR_DATA_MAP.get(year);
        if (dataMap == null) {
            LogUtils.error((String)"\u6ca1\u6709\u5bf9\u5e94\u5e74:[{}]\u7684\u6570\u636e\uff0c\u8bf7\u5347\u7ea7\u6216\u8005\u81ea\u884c\u7ef4\u62a4\u6570\u636e\uff01", (Object[])new Object[]{year});
            return HolidaysApiImpl.isWeekDay(localDate);
        }
        int monthValue = localDate.getMonthValue();
        int dayOfMonth = localDate.getDayOfMonth();
        String monthAndDay = String.format("%02d%02d", monthValue, dayOfMonth);
        Byte result = dataMap.get(monthAndDay);
        if (result != null) {
            return DaysType.from(result);
        }
        return HolidaysApiImpl.isWeekDay(localDate);
    }

    public void afterPropertiesSet() throws Exception {
        Map dataMap;
        InputStream inputStream;
        Resource resource;
        int[] years;
        for (int year : years = new int[]{2019, 2020, 2021, 2022, 2023, 2024, 2025}) {
            resource = this.resourceLoader.getResource("classpath:data/" + year + "_data.json");
            inputStream = resource.getInputStream();
            try {
                dataMap = JacksonUtils.readMap((InputStream)inputStream, Byte.class);
                YEAR_DATA_MAP.put(year, dataMap);
            }
            finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        List<HolidaysApiProperties.ExtData> extDataList = this.properties.getExtData();
        for (HolidaysApiProperties.ExtData extData : extDataList) {
            String dataPath = extData.getDataPath();
            resource = this.resourceLoader.getResource(dataPath);
            inputStream = resource.getInputStream();
            try {
                dataMap = JacksonUtils.readMap((InputStream)inputStream, Byte.class);
                YEAR_DATA_MAP.put(extData.getYear(), dataMap);
            }
            finally {
                if (inputStream == null) continue;
                inputStream.close();
            }
        }
    }

    private static DaysType isWeekDay(LocalDate localDate) {
        int week = localDate.getDayOfWeek().getValue();
        return week == 6 || week == 7 ? DaysType.REST_DAYS : DaysType.WEEKDAYS;
    }
}

