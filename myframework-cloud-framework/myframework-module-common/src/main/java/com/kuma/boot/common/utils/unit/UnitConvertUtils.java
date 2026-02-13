/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.unit;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.unit.BigDecimalConvert;
import com.kuma.boot.common.utils.unit.UnitConvertType;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UnitConvertUtils {
    public static <T> void unitAnnotateConvert(List<T> list) {
        for (T t : list) {
            Field[] declaredFields;
            for (Field declaredField : declaredFields = t.getClass().getDeclaredFields()) {
                try {
                    BigDecimal bigDecimal;
                    BigDecimalConvert myFieldAnn;
                    if ("serialVersionUID".equals(declaredField.getName()) || Objects.isNull(myFieldAnn = declaredField.getAnnotation(BigDecimalConvert.class))) continue;
                    UnitConvertType unitConvertType = myFieldAnn.name();
                    declaredField.setAccessible(true);
                    Object o = declaredField.get(t);
                    if (!Objects.nonNull(o)) continue;
                    if (unitConvertType.equals((Object)UnitConvertType.PERCENTAGE)) {
                        bigDecimal = ((BigDecimal)o).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                        declaredField.set(t, bigDecimal);
                    }
                    if (unitConvertType.equals((Object)UnitConvertType.PERMIL)) {
                        bigDecimal = ((BigDecimal)o).multiply(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP);
                        declaredField.set(t, bigDecimal);
                    }
                    if (unitConvertType.equals((Object)UnitConvertType.B)) {
                        bigDecimal = ((BigDecimal)o).divide(new BigDecimal(10000), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
                        declaredField.set(t, bigDecimal);
                    }
                    if (!unitConvertType.equals((Object)UnitConvertType.R)) continue;
                    bigDecimal = ((BigDecimal)o).setScale(2, RoundingMode.HALF_UP);
                    declaredField.set(t, bigDecimal);
                }
                catch (Exception ex) {
                    LogUtils.error("\u5904\u7406\u5931\u8d25", new Object[0]);
                }
            }
        }
    }

    public static <T> void unitMapConvert(List<T> list, Map<String, UnitConvertType> propertyMap) {
        for (T t : list) {
            Field[] declaredFields;
            for (Field declaredField : declaredFields = t.getClass().getDeclaredFields()) {
                if (!propertyMap.keySet().stream().anyMatch(x -> x.equals(declaredField.getName()))) continue;
                try {
                    BigDecimal bigDecimal;
                    declaredField.setAccessible(true);
                    Object o = declaredField.get(t);
                    UnitConvertType unitConvertType = propertyMap.get(declaredField.getName());
                    if (o == null) continue;
                    if (unitConvertType.equals((Object)UnitConvertType.PERCENTAGE)) {
                        bigDecimal = ((BigDecimal)o).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                        declaredField.set(t, bigDecimal);
                    }
                    if (unitConvertType.equals((Object)UnitConvertType.PERMIL)) {
                        bigDecimal = ((BigDecimal)o).multiply(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP);
                        declaredField.set(t, bigDecimal);
                    }
                    if (unitConvertType.equals((Object)UnitConvertType.B)) {
                        bigDecimal = ((BigDecimal)o).divide(new BigDecimal(10000), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
                        declaredField.set(t, bigDecimal);
                    }
                    if (!unitConvertType.equals((Object)UnitConvertType.R)) continue;
                    bigDecimal = ((BigDecimal)o).setScale(2, RoundingMode.HALF_UP);
                    declaredField.set(t, bigDecimal);
                }
                catch (Exception ex) {
                    LogUtils.error("\u5904\u7406\u5931\u8d25", new Object[0]);
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
        ArrayList<MyYearSumReportDTO> list = new ArrayList<MyYearSumReportDTO>();
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
        ArrayList<MySumReportDTO> list = new ArrayList<MySumReportDTO>();
        list.add(mySumReportDTO);
        list.add(mySumReportDTO1);
        return list;
    }

    public static class MyYearSumReportDTO
    implements Serializable {
        private static final long serialVersionUID = 5285987517581372888L;
        @BigDecimalConvert(name=UnitConvertType.B)
        private BigDecimal payTotalAmount;
        @BigDecimalConvert(name=UnitConvertType.PERCENTAGE)
        private BigDecimal jcAmountPercentage;
        @BigDecimalConvert(name=UnitConvertType.PERMIL)
        private BigDecimal jcCountPermillage;
        @BigDecimalConvert(name=UnitConvertType.R)
        private BigDecimal length;
        @BigDecimalConvert(name=UnitConvertType.R)
        private BigDecimal width;

        public BigDecimal getPayTotalAmount() {
            return this.payTotalAmount;
        }

        public void setPayTotalAmount(BigDecimal payTotalAmount) {
            this.payTotalAmount = payTotalAmount;
        }

        public BigDecimal getJcAmountPercentage() {
            return this.jcAmountPercentage;
        }

        public void setJcAmountPercentage(BigDecimal jcAmountPercentage) {
            this.jcAmountPercentage = jcAmountPercentage;
        }

        public BigDecimal getJcCountPermillage() {
            return this.jcCountPermillage;
        }

        public void setJcCountPermillage(BigDecimal jcCountPermillage) {
            this.jcCountPermillage = jcCountPermillage;
        }

        public BigDecimal getLength() {
            return this.length;
        }

        public void setLength(BigDecimal length) {
            this.length = length;
        }

        public BigDecimal getWidth() {
            return this.width;
        }

        public void setWidth(BigDecimal width) {
            this.width = width;
        }
    }

    public static class MySumReportDTO
    implements Serializable {
        private BigDecimal payTotalAmount;
        private BigDecimal jcAmountPercentage;
        private BigDecimal jcCountPermillage;
        private BigDecimal length;
        private BigDecimal width;

        public BigDecimal getPayTotalAmount() {
            return this.payTotalAmount;
        }

        public void setPayTotalAmount(BigDecimal payTotalAmount) {
            this.payTotalAmount = payTotalAmount;
        }

        public BigDecimal getJcAmountPercentage() {
            return this.jcAmountPercentage;
        }

        public void setJcAmountPercentage(BigDecimal jcAmountPercentage) {
            this.jcAmountPercentage = jcAmountPercentage;
        }

        public BigDecimal getJcCountPermillage() {
            return this.jcCountPermillage;
        }

        public void setJcCountPermillage(BigDecimal jcCountPermillage) {
            this.jcCountPermillage = jcCountPermillage;
        }

        public BigDecimal getLength() {
            return this.length;
        }

        public void setLength(BigDecimal length) {
            this.length = length;
        }

        public BigDecimal getWidth() {
            return this.width;
        }

        public void setWidth(BigDecimal width) {
            this.width = width;
        }
    }
}

