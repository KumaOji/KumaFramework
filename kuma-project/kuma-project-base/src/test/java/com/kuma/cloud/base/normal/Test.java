package com.kuma.cloud.base.normal;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author 曾帅
 * @version 1.0
 */
public class Test {
    public static void main(String[] args){
//        assertEquals("ttlheoiscstk", tripleTrouble("this", "test", "lock"));
//        tripleTrouble("this", "test", "lock");
//        step(2,110,110);
    }

    public static String tripleTrouble(String one, String two, String three) {
        String res = "";

        for (int i = 0; i < one.length(); i++) {

            res += one.charAt(i);
            res += two.charAt(i);
            res += three.charAt(i);
        }
        return res;
    }

    public static long[] step2(int g, long m, long n) {
        for (long i = m; i <= n; i++) {
            if (java.math.BigInteger.valueOf(                                                                                                                         i).isProbablePrime(5) &&
                    java.math.BigInteger.valueOf(i + g).isProbablePrime(5)) {
                return new long[] {i, i + g};
            }
        }
        return null;
    }

    public static long[] step(int g, long m, long n) {
        for (long i = m; i + g <= n; i++) {
            if (isPrime(i) && isPrime(i + g)) {
                return new long[]{i, i + g};
            }
        }
        return null;
    }

    private static boolean isPrime(long num) {
        if (num < 2) return false;
        for (long i = 2; i * i <= num; i++) {
            if (num % i == 0) return false;
        }
        return true;
    }
}
