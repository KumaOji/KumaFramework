//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.utils.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * HexUtil
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class HexUtil {

    private static final String HEX_NUMS_STR = "0123456789ABCDEF";

    private HexUtil() {
    }

    public static byte[] hexStringToByte( String hex ) {
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] hexChars = hex.toCharArray();

        for (int i = 0; i < len; ++i) {
            int pos = i * 2;
            result[i] = (byte) ( "0123456789ABCDEF".indexOf(hexChars[pos]) << 4 | "0123456789ABCDEF".indexOf(
                    hexChars[pos + 1]) );
        }

        return result;
    }

    public static String byteToHexString( byte[] b ) {
        StringBuilder hexString = new StringBuilder();

        for (byte value : b) {
            String hex = Integer.toHexString(value & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            hexString.append(hex.toUpperCase());
        }

        return hexString.toString();
    }

    public static String genSaltHex() {
        return genSaltHex(16);
    }

    public static String genSaltHex( int saltSize ) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltSize];
        random.nextBytes(salt);
        return byteToHexString(salt);
    }

    public static String encrypt( String password, String saltHex ) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hexStringToByte(saltHex));
            md.update(password.getBytes("UTF-8"));
            byte[] digest = md.digest();
            return byteToHexString(digest);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new SecurityException(e);
        }
    }

    public static boolean isValid( String password, String saltHex, String secretHex ) {
        if (StringUtils.isEmpty(password)) {
            return false;
        } else {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(hexStringToByte(saltHex));
                md.update(password.getBytes("UTF-8"));
                byte[] digest = md.digest();
                byte[] old = hexStringToByte(secretHex);
                return Arrays.equals(digest, old);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                throw new SecurityException(e);
            }
        }
    }

    public static void main( String[] args ) {
        String password = "123456";
        String salt = genSaltHex();
        System.out.println(salt);
        String secret = encrypt(password, salt);
        System.out.println(secret);
        System.out.println(isValid(password, salt, secret));
    }
}
