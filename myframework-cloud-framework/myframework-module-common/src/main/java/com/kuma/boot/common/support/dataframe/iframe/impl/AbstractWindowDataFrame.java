/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.impl;

import com.kuma.boot.common.support.dataframe.iframe.SDFrame;
import com.kuma.boot.common.support.dataframe.iframe.impl.AbstractCommonFrame;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.window.SupplierFunction;
import com.kuma.boot.common.support.dataframe.iframe.window.Window;
import com.kuma.boot.common.support.dataframe.iframe.window.WindowBuilder;
import com.kuma.boot.common.support.dataframe.iframe.window.round.Range;
import com.kuma.boot.common.support.dataframe.util.FieldValueList;
import com.kuma.boot.common.support.dataframe.util.ListUtils;
import com.kuma.boot.common.support.dataframe.util.MathUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractWindowDataFrame<T>
extends AbstractCommonFrame<T> {
    protected final Window<T> emptyWindow = new WindowBuilder(Range.START_ROW, Range.END_ROW);
    protected Window<T> window;

    protected <V> List<FI2<T, V>> overAbject(Window<T> overParam, SupplierFunction<T, V> supplier) {
        ((WindowBuilder)overParam).initDefault();
        List windowList = this.viewList();
        ArrayList<FI2<T, V>> result = new ArrayList<FI2<T, V>>();
        if (ListUtils.isEmpty(windowList)) {
            return result;
        }
        Comparator<T> comparator = overParam.getComparator();
        List<Function<T, ?>> partitionList = overParam.partitions();
        if (ListUtils.isEmpty(partitionList)) {
            if (comparator != null) {
                windowList.sort(comparator);
            }
            return supplier.get(windowList);
        }
        ArrayList<List<T>> allWindowList = new ArrayList<List<T>>();
        this.dfsFindWindow(allWindowList, windowList, partitionList, 0);
        for (List list : allWindowList) {
            if (comparator != null) {
                list.sort(comparator);
            }
            List tmpList = supplier.get(list);
            result.addAll(tmpList);
        }
        return result;
    }

    protected void dfsFindWindow(List<List<T>> result, List<T> windowList, List<Function<T, ?>> partitionList, int index) {
        if (index >= partitionList.size()) {
            result.add(windowList);
            return;
        }
        Function<T, ?> partitionBy = partitionList.get(index);
        Map<?, List<T>> collect = windowList.stream().collect(Collectors.groupingBy(partitionBy));
        for (List<T> window : collect.values()) {
            this.dfsFindWindow(result, window, partitionList, index + 1);
        }
    }

    protected List<FI2<T, Integer>> windowFunctionForRowNumber(Window<T> overParam) {
        SupplierFunction supplier = windowList -> {
            ArrayList result = new ArrayList();
            int index = 1;
            for (Object t : windowList) {
                result.add(new FI2(t, index++));
            }
            return result;
        };
        return this.overAbject(overParam, supplier);
    }

    protected List<FI2<T, Integer>> windowFunctionForRank(Window<T> overParam) {
        this.checkWindow(overParam);
        SupplierFunction supplier = windowList -> {
            ArrayList result = new ArrayList();
            int n = windowList.size();
            int rank = 1;
            result.add(new FI2(windowList.get(0), 1));
            for (int i = 1; i < windowList.size(); ++i) {
                Object pre = windowList.get(i - 1);
                Object cur = windowList.get(i);
                if (overParam.getComparator().compare(pre, cur) != 0) {
                    rank = i + 1;
                }
                if (rank > n) break;
                result.add(new FI2(cur, rank));
            }
            return result;
        };
        return this.overAbject(overParam, supplier);
    }

    protected List<FI2<T, Integer>> windowFunctionForDenseRank(Window<T> overParam) {
        this.checkWindow(overParam);
        SupplierFunction supplier = windowList -> {
            ArrayList result = new ArrayList();
            int n = windowList.size();
            int rank = 1;
            result.add(new FI2(windowList.get(0), 1));
            for (int i = 1; i < windowList.size(); ++i) {
                Object pre = windowList.get(i - 1);
                Object cur = windowList.get(i);
                if (overParam.getComparator().compare(pre, cur) != 0) {
                    ++rank;
                }
                if (rank > n) break;
                result.add(new FI2(cur, rank));
            }
            return result;
        };
        return this.overAbject(overParam, supplier);
    }

    protected List<FI2<T, BigDecimal>> windowFunctionForPercentRank(Window<T> overParam) {
        this.checkWindow(overParam);
        SupplierFunction supplier = windowList -> {
            ArrayList result = new ArrayList();
            int n = windowList.size();
            int rank = 1;
            result.add(new FI2(windowList.get(0), BigDecimal.ZERO));
            for (int i = 1; i < windowList.size(); ++i) {
                Object pre = windowList.get(i - 1);
                Object cur = windowList.get(i);
                if (overParam.getComparator().compare(pre, cur) != 0) {
                    rank = i + 1;
                }
                if (rank > n) break;
                BigDecimal divide = MathUtils.divide(rank - 1, windowList.size() - 1, this.defaultScale, this.defaultRoundingMode);
                result.add(new FI2(cur, divide));
            }
            return result;
        };
        return this.overAbject(overParam, supplier);
    }

    protected List<FI2<T, BigDecimal>> windowFunctionForCumeDist(Window<T> overParam) {
        this.checkWindow(overParam);
        SupplierFunction supplier = windowList -> {
            ArrayList result = new ArrayList();
            int n = windowList.size();
            int rank = 1;
            HashMap<Integer, Integer> rankCountMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < windowList.size(); ++i) {
                Object pre = i > 0 ? (Object)windowList.get(i - 1) : null;
                Object cur = windowList.get(i);
                if (pre != null && overParam.getComparator().compare(pre, cur) != 0) {
                    rankCountMap.put(rank, i);
                    rank = i + 1;
                }
                if (rank > n) break;
                result.add(new FI2(cur, rank));
            }
            rankCountMap.computeIfAbsent(rank, k -> windowList.size());
            ArrayList resultList = new ArrayList();
            result.forEach(e -> {
                Integer count = (Integer)rankCountMap.get(e.getC2());
                BigDecimal divide = MathUtils.divide(count, windowList.size(), this.defaultScale, this.defaultRoundingMode);
                resultList.add(new FI2(e.getC1(), divide));
            });
            return resultList;
        };
        return this.overAbject(overParam, supplier);
    }

    private void checkWindow(Window<T> overParam) {
        Comparator<T> comparator = overParam.getComparator();
        if (comparator == null) {
            throw new IllegalArgumentException("please specify window sort");
        }
    }

    protected <F> List<FI2<T, F>> windowFunctionForLag(Window<T> overParam, Function<T, F> field, int n) {
        SupplierFunction supplier = windowList -> {
            ArrayList result = new ArrayList();
            for (int i = 0; i < windowList.size(); ++i) {
                int preIndex = i - n;
                if (preIndex < 0) {
                    result.add(new FI2(windowList.get(i), null));
                    continue;
                }
                FI2<Integer, Integer> indexRange = this.getIndexRange(overParam, i, windowList);
                if (!this.isInRange(indexRange, preIndex)) {
                    preIndex = -1;
                }
                Object value = null;
                if (preIndex >= 0 && preIndex < windowList.size()) {
                    value = field.apply(windowList.get(preIndex));
                }
                result.add(new FI2(windowList.get(i), value));
            }
            return result;
        };
        return this.overAbject(overParam, supplier);
    }

    protected <F> List<FI2<T, F>> windowFunctionForLead(Window<T> overParam, Function<T, F> field, int n) {
        SupplierFunction supplier = windowList -> {
            ArrayList result = new ArrayList();
            for (int i = 0; i < windowList.size(); ++i) {
                int afterIndex = i + n;
                FI2<Integer, Integer> indexRange = this.getIndexRange(overParam, i, windowList);
                if (!this.isInRange(indexRange, afterIndex)) {
                    afterIndex = -1;
                }
                Object value = null;
                if (afterIndex >= 0 && afterIndex < windowList.size()) {
                    value = field.apply(windowList.get(afterIndex));
                }
                result.add(new FI2(windowList.get(i), value));
            }
            return result;
        };
        return this.overAbject(overParam, supplier);
    }

    protected <F> List<FI2<T, F>> windowFunctionForNthValue(Window<T> overParam, Function<T, F> field, int n) {
        SupplierFunction supplier = windowList -> {
            int index = n == -1 ? windowList.size() - 1 : n - 1;
            if (index < 0 || index >= windowList.size()) {
                return windowList.stream().map((? super T e) -> new FI2<Object, Object>(e, null)).collect(Collectors.toList());
            }
            ArrayList result = new ArrayList();
            for (int i = 0; i < windowList.size(); ++i) {
                Object value = null;
                FI2<Integer, Integer> indexRange = this.getIndexRange(overParam, i, windowList);
                if (indexRange.getC1() < 0) {
                    indexRange.setC1(0);
                }
                if ((index = n != -1 ? indexRange.getC1() + n - 1 : indexRange.getC2()) >= 0 && index < windowList.size() && this.isInRange(indexRange, index)) {
                    value = field.apply(windowList.get(index));
                }
                result.add(new FI2(windowList.get(i), value));
            }
            return result;
        };
        return this.overAbject(overParam, supplier);
    }

    public <V> FI2<Integer, Integer> getIndexRange(Window<T> overParam, int currentIndex, List<V> windowList) {
        Integer startIndex = overParam.getStartRange().getStartIndex(currentIndex, windowList);
        Integer endIndex = overParam.getEndRange().getEndIndex(currentIndex, windowList);
        return new FI2<Integer, Integer>(startIndex, endIndex);
    }

    public boolean isInRange(FI2<Integer, Integer> round, int index) {
        return index >= round.getC1() && index <= round.getC2();
    }

    public boolean isAllRow(Window<T> overParam) {
        return Range.START_ROW.equals(overParam.getStartRange()) && Range.END_ROW.equals(overParam.getEndRange());
    }

    protected <F> List<FI2<T, BigDecimal>> windowFunctionForSum(Window<T> overParam, Function<T, F> field) {
        SupplierFunction supplier = windowList -> {
            if (this.isAllRow(overParam)) {
                BigDecimal value = SDFrame.read(windowList).sum(field);
                return windowList.stream().map((? super T e) -> new FI2<Object, BigDecimal>(e, value)).collect(Collectors.toList());
            }
            return this.slidingWindowSum(windowList, overParam, field);
        };
        return this.overAbject(overParam, supplier);
    }

    public <F> List<FI2<T, BigDecimal>> slidingWindowSum(List<T> nums, Window<T> overParam, Function<T, F> field) {
        FI2<Integer, Integer> firstSlidingWindow = this.getFirstSlidingWindow(nums, overParam);
        Integer startIndex = firstSlidingWindow.getC1();
        Integer endIndex = firstSlidingWindow.getC2();
        BigDecimal windowSum = BigDecimal.ZERO;
        for (int i = startIndex.intValue(); i <= endIndex && i < nums.size(); ++i) {
            if (i < 0) continue;
            windowSum = windowSum.add(this.getBigDecimalValue(nums.get(i), field));
        }
        ArrayList<FI2<T, BigDecimal>> dataList = new ArrayList<FI2<T, BigDecimal>>();
        dataList.add(new FI2<T, BigDecimal>(nums.get(0), windowSum));
        int index = 1;
        while (dataList.size() < nums.size()) {
            if (!overParam.getEndRange().isFixedEndIndex() && (endIndex = Integer.valueOf(endIndex + 1)) >= 0 && endIndex < nums.size()) {
                windowSum = windowSum.add(this.getBigDecimalValue(nums.get(endIndex), field));
            }
            if (!overParam.getStartRange().isFixedStartIndex()) {
                if (startIndex >= 0 && startIndex < nums.size()) {
                    windowSum = windowSum.subtract(this.getBigDecimalValue(nums.get(startIndex), field));
                }
                Integer n = startIndex;
                startIndex = startIndex + 1;
            }
            if (endIndex < 0) continue;
            dataList.add(new FI2<T, BigDecimal>(nums.get(index++), windowSum));
        }
        return dataList;
    }

    public <F> BigDecimal getBigDecimalValue(T obj, Function<T, F> field) {
        F apply = field.apply(obj);
        if (apply == null) {
            return BigDecimal.ZERO;
        }
        if (apply instanceof BigDecimal) {
            return (BigDecimal)apply;
        }
        return new BigDecimal(String.valueOf(apply));
    }

    protected <F> List<FI2<T, BigDecimal>> windowFunctionForAvg(Window<T> overParam, Function<T, F> field) {
        SupplierFunction supplier = windowList -> {
            if (this.isAllRow(overParam)) {
                BigDecimal value = SDFrame.read(windowList).defaultScale(this.defaultScale, this.defaultRoundingMode).avg(field);
                return windowList.stream().map((? super T e) -> new FI2<Object, BigDecimal>(e, value)).collect(Collectors.toList());
            }
            return this.slidingWindowAvg(windowList, overParam, field);
        };
        return this.overAbject(overParam, supplier);
    }

    public <F> List<FI2<T, BigDecimal>> slidingWindowAvg(List<T> nums, Window<T> overParam, Function<T, F> field) {
        FI2<Integer, Integer> firstSlidingWindow = this.getFirstSlidingWindow(nums, overParam);
        Integer startIndex = firstSlidingWindow.getC1();
        Integer endIndex = firstSlidingWindow.getC2();
        BigDecimal windowSum = BigDecimal.ZERO;
        int windowSize = 0;
        for (int i = startIndex.intValue(); i <= endIndex && i < nums.size(); ++i) {
            if (i < 0) continue;
            ++windowSize;
            windowSum = windowSum.add(this.getBigDecimalValue(nums.get(i), field));
        }
        ArrayList<FI2<T, BigDecimal>> dataList = new ArrayList<FI2<T, BigDecimal>>();
        dataList.add(new FI2<T, BigDecimal>(nums.get(0), MathUtils.divide(windowSum, new BigDecimal(windowSize), this.defaultScale, this.defaultRoundingMode)));
        int index = 1;
        while (dataList.size() < nums.size()) {
            if (!overParam.getEndRange().isFixedEndIndex() && (endIndex = Integer.valueOf(endIndex + 1)) >= 0 && endIndex < nums.size()) {
                windowSum = windowSum.add(this.getBigDecimalValue(nums.get(endIndex), field));
            }
            if (!overParam.getStartRange().isFixedStartIndex()) {
                if (startIndex >= 0 && startIndex < nums.size()) {
                    windowSum = windowSum.subtract(this.getBigDecimalValue(nums.get(startIndex), field));
                }
                Integer n = startIndex;
                startIndex = startIndex + 1;
            }
            windowSize = this.getActualWindowSize(nums, startIndex, endIndex);
            if (endIndex < 0) continue;
            dataList.add(new FI2<T, BigDecimal>(nums.get(index++), MathUtils.divide(windowSum, new BigDecimal(windowSize), this.defaultScale, this.defaultRoundingMode)));
        }
        return dataList;
    }

    private Integer getActualWindowSize(List<T> nums, Integer startIndex, Integer endIndex) {
        if (endIndex < 0 || startIndex >= nums.size()) {
            return 0;
        }
        if (startIndex < 0 && endIndex >= nums.size()) {
            return nums.size();
        }
        int left = startIndex < 0 ? 0 : startIndex;
        int right = endIndex >= nums.size() ? nums.size() - 1 : endIndex;
        return right - left + 1;
    }

    public FI2<Integer, Integer> getFirstSlidingWindow(List<T> windowList, Window<T> overParam) {
        return this.getIndexRange(overParam, 0, windowList);
    }

    public <F extends Comparable<? super F>> void updateSlidingWindowMaxQueue(LinkedList<Integer> queue, FieldValueList<T, F> obj, int i) {
        while (!queue.isEmpty() && ((Comparable)obj.get(queue.peekLast())).compareTo(obj.get(i)) < 0) {
            queue.removeLast();
        }
        queue.add(i);
    }

    public <F extends Comparable<? super F>> void updateSlidingWindowMinQueue(LinkedList<Integer> queue, FieldValueList<T, F> obj, int i) {
        while (!queue.isEmpty() && ((Comparable)obj.get(queue.peekLast())).compareTo(obj.get(i)) > 0) {
            queue.removeLast();
        }
        queue.add(i);
    }

    public <F extends Comparable<? super F>> List<FI2<T, F>> slidingWindowForMaxValue(List<T> nums, Window<T> overParam, Function<T, F> field) {
        FI2<Integer, Integer> firstSlidingWindow = this.getFirstSlidingWindow(nums, overParam);
        Integer startIndex = firstSlidingWindow.getC1();
        Integer endIndex = firstSlidingWindow.getC2();
        FieldValueList<T, F> obj = new FieldValueList<T, F>(nums, field);
        LinkedList<Integer> queue = new LinkedList<Integer>();
        for (int i = startIndex.intValue(); i <= endIndex && i < nums.size(); ++i) {
            if (i < 0) continue;
            this.updateSlidingWindowMaxQueue(queue, obj, i);
        }
        ArrayList<FI2<T, F>> dataList = new ArrayList<FI2<T, F>>();
        Integer maxIndex = (Integer)queue.peekFirst();
        dataList.add(new FI2<T, Comparable>(nums.get(0), (Comparable)obj.get(maxIndex)));
        int index = 1;
        while (dataList.size() < nums.size()) {
            if (!overParam.getEndRange().isFixedEndIndex() && (endIndex = Integer.valueOf(endIndex + 1)) >= 0 && endIndex < nums.size()) {
                this.updateSlidingWindowMaxQueue(queue, obj, endIndex);
            }
            if (!overParam.getStartRange().isFixedStartIndex()) {
                Integer n = startIndex;
                startIndex = startIndex + 1;
            }
            while (!queue.isEmpty() && queue.peekFirst() < startIndex) {
                queue.removeFirst();
            }
            if (endIndex < 0) continue;
            dataList.add(new FI2<T, Comparable>(nums.get(index++), (Comparable)obj.get(queue.peekFirst())));
        }
        return dataList;
    }

    public <F extends Comparable<? super F>> List<FI2<T, F>> slidingWindowForMinValue(List<T> nums, Window<T> overParam, Function<T, F> field) {
        FI2<Integer, Integer> firstSlidingWindow = this.getFirstSlidingWindow(nums, overParam);
        Integer startIndex = firstSlidingWindow.getC1();
        Integer endIndex = firstSlidingWindow.getC2();
        FieldValueList<T, F> obj = new FieldValueList<T, F>(nums, field);
        LinkedList<Integer> queue = new LinkedList<Integer>();
        for (int i = startIndex.intValue(); i <= endIndex && i < nums.size(); ++i) {
            if (i < 0) continue;
            this.updateSlidingWindowMinQueue(queue, obj, i);
        }
        ArrayList<FI2<T, F>> dataList = new ArrayList<FI2<T, F>>();
        Integer maxIndex = (Integer)queue.peekFirst();
        dataList.add(new FI2<T, Comparable>(nums.get(0), (Comparable)obj.get(maxIndex)));
        int index = 1;
        while (dataList.size() < nums.size()) {
            if (!overParam.getEndRange().isFixedEndIndex() && (endIndex = Integer.valueOf(endIndex + 1)) >= 0 && endIndex < nums.size()) {
                this.updateSlidingWindowMinQueue(queue, obj, endIndex);
            }
            if (!overParam.getStartRange().isFixedStartIndex()) {
                Integer n = startIndex;
                startIndex = startIndex + 1;
            }
            while (!queue.isEmpty() && queue.peekFirst() < startIndex) {
                queue.removeFirst();
            }
            if (endIndex < 0) continue;
            dataList.add(new FI2<T, Comparable>(nums.get(index++), (Comparable)obj.get(queue.peekFirst())));
        }
        return dataList;
    }

    protected <F extends Comparable<? super F>> List<FI2<T, F>> windowFunctionForMaxValue(Window<T> overParam, Function<T, F> field) {
        SupplierFunction supplier = windowList -> {
            if (this.isAllRow(overParam)) {
                Object value = SDFrame.read(windowList).maxValue(field);
                return windowList.stream().map((? super T e) -> new FI2<Object, Comparable>(e, (Comparable)value)).collect(Collectors.toList());
            }
            return this.slidingWindowForMaxValue(windowList, overParam, field);
        };
        return this.overAbject(overParam, supplier);
    }

    protected <F extends Comparable<? super F>> List<FI2<T, F>> windowFunctionForMinValue(Window<T> overParam, Function<T, F> field) {
        SupplierFunction supplier = windowList -> {
            if (this.isAllRow(overParam)) {
                Object value = SDFrame.read(windowList).minValue(field);
                return windowList.stream().map((? super T e) -> new FI2<Object, Comparable>(e, (Comparable)value)).collect(Collectors.toList());
            }
            return this.slidingWindowForMinValue(windowList, overParam, field);
        };
        return this.overAbject(overParam, supplier);
    }

    protected List<FI2<T, Integer>> windowFunctionForCount(Window<T> overParam) {
        SupplierFunction supplier = windowList -> {
            if (this.isAllRow(overParam)) {
                int count = windowList.size();
                return windowList.stream().map((? super T e) -> new FI2<Object, Integer>(e, count)).collect(Collectors.toList());
            }
            ArrayList result = new ArrayList();
            for (int i = 0; i < windowList.size(); ++i) {
                FI2<Integer, Integer> indexRange = this.getIndexRange(overParam, i, windowList);
                if (indexRange.getC1() <= 0) {
                    indexRange.setC1(0);
                }
                if (indexRange.getC2() > windowList.size() - 1) {
                    indexRange.setC2(windowList.size() - 1);
                }
                Integer value = indexRange.getC2() - indexRange.getC1() + 1;
                result.add(new FI2(windowList.get(i), value));
            }
            return result;
        };
        return this.overAbject(overParam, supplier);
    }

    protected List<FI2<T, Integer>> windowFunctionForNtile(Window<T> overParam, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("incorrect arguments to ntile for " + n);
        }
        SupplierFunction supplier = windowList -> {
            ArrayList result = new ArrayList();
            if (windowList.size() % n == 0) {
                int groupSize = windowList.size() / n;
                int bucket = 1;
                int index = 0;
                for (Object t : windowList) {
                    if (++index > groupSize) {
                        ++bucket;
                        index = 1;
                    }
                    result.add(new FI2(t, bucket));
                }
                return result;
            }
            int[] arr = new int[n];
            int index = 0;
            for (int count = windowList.size(); count > 0; --count) {
                if (index >= arr.length) {
                    index = 0;
                }
                int n2 = index++;
                arr[n2] = arr[n2] + 1;
            }
            int bucket = 1;
            for (int i = 0; i < windowList.size(); ++i) {
                int n3 = bucket - 1;
                arr[n3] = arr[n3] - 1;
                result.add(new FI2(windowList.get(i), bucket));
                if (arr[bucket - 1] > 0) continue;
                ++bucket;
            }
            return result;
        };
        return this.overAbject(overParam, supplier);
    }
}

