package com.kuma.cloud.base.normal;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.number.DecimalNum;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author 曾帅
 * @version 1.0
 */
public class Define {
    public static void main(String[] args){
        long[] longs = {1,2};
        int[] ints  = {1,2};
        String[] strings = {"a","b"};
        Character[] characters = {'a','b'};
        float[] floats={1.1F, 1.2F};
        double[] doubles = {1.1D,1.2D};
        List<Integer> list = Arrays.asList(1, 2);
        Stream<Integer> integerStream = Stream.of(1, 2);
        IntStream stream = Arrays.stream(new int[]{1, 2});

        BigInteger bigInteger = BigInteger.valueOf(123);
        BigDecimal bigDecimal = BigDecimal.valueOf(123);
        BigDecimal bigDecimal1 = new BigDecimal(123);


        DecimalNum decimalNum = new DecimalNum(new BigDecimal(46156151));
    }
}
