/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.utils.unit;

import com.kuma.boot.common.utils.log.LogUtils;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * UnitConvertUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class UnitConvertUtils {

    public static <T> void unitAnnotateConvert( List<T> list ) {
        for (T t : list) {
            Field[] declaredFields = t.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                try {
                    if ("serialVersionUID".equals(declaredField.getName())) {
                        continue;
                    }
                    BigDecimalConvert myFieldAnn =
                            declaredField.getAnnotation(BigDecimalConvert.class);
                    if (Objects.isNull(myFieldAnn)) {
                        continue;
                    }
                    UnitConvertType unitConvertType = myFieldAnn.name();
                    declaredField.setAccessible(true);
                    Object o = declaredField.get(t);
                    if (Objects.nonNull(o)) {
                        if (unitConvertType.equals(UnitConvertType.PERCENTAGE)) {
                            BigDecimal bigDecimal =
                                    ( (BigDecimal) o )
                                            .multiply(new BigDecimal(100))
                                            .setScale(2, RoundingMode.HALF_UP);
                            declaredField.set(t, bigDecimal);
                        }
                        if (unitConvertType.equals(UnitConvertType.PERMIL)) {
                            BigDecimal bigDecimal =
                                    ( (BigDecimal) o )
                                            .multiply(new BigDecimal(1000))
                                            .setScale(2, RoundingMode.HALF_UP);
                            declaredField.set(t, bigDecimal);
                        }
                        if (unitConvertType.equals(UnitConvertType.B)) {
                            BigDecimal bigDecimal =
                                    ( (BigDecimal) o )
                                            .divide(new BigDecimal(10000), RoundingMode.HALF_UP)
                                            .setScale(2, RoundingMode.HALF_UP);
                            declaredField.set(t, bigDecimal);
                        }
                        if (unitConvertType.equals(UnitConvertType.R)) {
                            BigDecimal bigDecimal =
                                    ( (BigDecimal) o ).setScale(2, RoundingMode.HALF_UP);
                            declaredField.set(t, bigDecimal);
                        }
                    }
                } catch (Exception ex) {
                    LogUtils.error("处理失败");
                }
            }
        }
    }

    public static <T> void unitMapConvert( List<T> list, Map<String, UnitConvertType> propertyMap ) {
        for (T t : list) {
            Field[] declaredFields = t.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (propertyMap.keySet().stream()
                        .anyMatch(x -> x.equals(declaredField.getName()))) {
                    try {
                        declaredField.setAccessible(true);
                        Object o = declaredField.get(t);
                        UnitConvertType unitConvertType = propertyMap.get(declaredField.getName());
                        if (o != null) {
                            if (unitConvertType.equals(UnitConvertType.PERCENTAGE)) {
                                BigDecimal bigDecimal =
                                        ( (BigDecimal) o )
                                                .multiply(new BigDecimal(100))
                                                .setScale(2, RoundingMode.HALF_UP);
                                declaredField.set(t, bigDecimal);
                            }
                            if (unitConvertType.equals(UnitConvertType.PERMIL)) {
                                BigDecimal bigDecimal =
                                        ( (BigDecimal) o )
                                                .multiply(new BigDecimal(1000))
                                                .setScale(2, RoundingMode.HALF_UP);
                                declaredField.set(t, bigDecimal);
                            }
                            if (unitConvertType.equals(UnitConvertType.B)) {
                                BigDecimal bigDecimal =
                                        ( (BigDecimal) o )
                                                .divide(new BigDecimal(10000), RoundingMode.HALF_UP)
                                                .setScale(2, RoundingMode.HALF_UP);
                                declaredField.set(t, bigDecimal);
                            }
                            if (unitConvertType.equals(UnitConvertType.R)) {
                                BigDecimal bigDecimal =
                                        ( (BigDecimal) o ).setScale(2, RoundingMode.HALF_UP);
                                declaredField.set(t, bigDecimal);
                            }
                        }
                    } catch (Exception ex) {
                        LogUtils.error("处理失败");
                        continue;
                    }
                }
            }
        }
    }

    private static List<MyYearSumReportDTO> getMyYearSumReportList() {
        MyYearSumReportDTO mySumReportDTO = new MyYearSumReportDTO();
        mySumReportDTO.setPayTotalAmount(new BigDecimal(1100000));
        mySumReportDTO.setJcAmountPercentage(BigDecimal.valueOf(0.695));
        mySumReportDTO.setJcCountPermillage(BigDecimal.valueOf(0.7894));
        mySumReportDTO.setLength(BigDecimal.valueOf(1300.65112));
        mySumReportDTO.setWidth(BigDecimal.valueOf(6522.12344));
        MyYearSumReportDTO mySumReportDTO1 = new MyYearSumReportDTO();
        mySumReportDTO1.setPayTotalAmount(new BigDecimal(2390000));
        mySumReportDTO1.setJcAmountPercentage(BigDecimal.valueOf(0.885));
        mySumReportDTO1.setJcCountPermillage(BigDecimal.valueOf(0.2394));
        mySumReportDTO1.setLength(BigDecimal.valueOf(1700.64003));
        mySumReportDTO1.setWidth(BigDecimal.valueOf(7522.12344));

        List<MyYearSumReportDTO> list = new ArrayList<>();
        list.add(mySumReportDTO);
        list.add(mySumReportDTO1);
        return list;
    }

    private static List<MySumReportDTO> getMySumReportList() {
        MySumReportDTO mySumReportDTO = new MySumReportDTO();
        mySumReportDTO.setPayTotalAmount(new BigDecimal(1100000));
        mySumReportDTO.setJcAmountPercentage(BigDecimal.valueOf(0.695));
        mySumReportDTO.setJcCountPermillage(BigDecimal.valueOf(0.7894));
        mySumReportDTO.setLength(BigDecimal.valueOf(1300.65112));
        mySumReportDTO.setWidth(BigDecimal.valueOf(6522.12344));

        MySumReportDTO mySumReportDTO1 = new MySumReportDTO();
        mySumReportDTO1.setPayTotalAmount(new BigDecimal(2390000));
        mySumReportDTO1.setJcAmountPercentage(BigDecimal.valueOf(0.885));
        mySumReportDTO1.setJcCountPermillage(BigDecimal.valueOf(0.2394));
        mySumReportDTO1.setLength(BigDecimal.valueOf(1700.64003));
        mySumReportDTO1.setWidth(BigDecimal.valueOf(7522.12344));

        List<MySumReportDTO> list = new ArrayList<>();

        list.add(mySumReportDTO);
        list.add(mySumReportDTO1);
        return list;
    }

    /**
     * MySumReportDTO
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class MySumReportDTO implements Serializable {

        private BigDecimal payTotalAmount;

        private BigDecimal jcAmountPercentage;

        private BigDecimal jcCountPermillage;

        private BigDecimal length;

        private BigDecimal width;

        public BigDecimal getPayTotalAmount() {
            return payTotalAmount;
        }

        public void setPayTotalAmount( BigDecimal payTotalAmount ) {
            this.payTotalAmount = payTotalAmount;
        }

        public BigDecimal getJcAmountPercentage() {
            return jcAmountPercentage;
        }

        public void setJcAmountPercentage( BigDecimal jcAmountPercentage ) {
            this.jcAmountPercentage = jcAmountPercentage;
        }

        public BigDecimal getJcCountPermillage() {
            return jcCountPermillage;
        }

        public void setJcCountPermillage( BigDecimal jcCountPermillage ) {
            this.jcCountPermillage = jcCountPermillage;
        }

        public BigDecimal getLength() {
            return length;
        }

        public void setLength( BigDecimal length ) {
            this.length = length;
        }

        public BigDecimal getWidth() {
            return width;
        }

        public void setWidth( BigDecimal width ) {
            this.width = width;
        }
    }

    /**
     * MyYearSumReportDTO
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class MyYearSumReportDTO implements Serializable {

        @Serial
        private static final long serialVersionUID = 5285987517581372888L;

        // 支付总金额
        @BigDecimalConvert(name = UnitConvertType.B)
        private BigDecimal payTotalAmount;

        // jc金额百分比
        @BigDecimalConvert(name = UnitConvertType.PERCENTAGE)
        private BigDecimal jcAmountPercentage;

        // jc计数千分比
        @BigDecimalConvert(name = UnitConvertType.PERMIL)
        private BigDecimal jcCountPermillage;

        // 保留2位
        @BigDecimalConvert(name = UnitConvertType.R)
        private BigDecimal length;

        // 保留2位
        @BigDecimalConvert(name = UnitConvertType.R)
        private BigDecimal width;

        public BigDecimal getPayTotalAmount() {
            return payTotalAmount;
        }

        public void setPayTotalAmount( BigDecimal payTotalAmount ) {
            this.payTotalAmount = payTotalAmount;
        }

        public BigDecimal getJcAmountPercentage() {
            return jcAmountPercentage;
        }

        public void setJcAmountPercentage( BigDecimal jcAmountPercentage ) {
            this.jcAmountPercentage = jcAmountPercentage;
        }

        public BigDecimal getJcCountPermillage() {
            return jcCountPermillage;
        }

        public void setJcCountPermillage( BigDecimal jcCountPermillage ) {
            this.jcCountPermillage = jcCountPermillage;
        }

        public BigDecimal getLength() {
            return length;
        }

        public void setLength( BigDecimal length ) {
            this.length = length;
        }

        public BigDecimal getWidth() {
            return width;
        }

        public void setWidth( BigDecimal width ) {
            this.width = width;
        }
    }
}
