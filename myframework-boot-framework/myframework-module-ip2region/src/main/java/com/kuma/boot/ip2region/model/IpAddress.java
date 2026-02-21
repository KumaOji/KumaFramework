/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ip2region.model;

import java.util.Arrays;

public class IpAddress {
    private byte[] mIp;
    private static final int INADDR4SZ = 4;
    private static final int INADDR16SZ = 16;
    private static final int INT16SZ = 2;

    public IpAddress(String ip) {
        this.mIp = IpAddress.textToNumericFormatV4(ip);
        if (this.mIp == null) {
            this.mIp = this.textToNumericFormatV6(ip);
        }
        if (this.mIp == null) {
            throw new IllegalArgumentException("invalid ip address `" + ip + "`");
        }
    }

    private IpAddress(byte[] mIp) {
        this.mIp = mIp;
    }

    public static IpAddress fromBytesV6(byte[] b) {
        if (b.length == 16) {
            return new IpAddress(b);
        }
        if (b.length < 16) {
            byte[] bs = new byte[16];
            System.arraycopy(b, 0, bs, 0, b.length);
            return new IpAddress(bs);
        }
        throw new IllegalArgumentException("\u975e\u6cd5IP\u5730\u5740");
    }

    public static IpAddress fromBytesV6LE(byte[] b) {
        byte[] be = IpAddress.bsWap(b);
        return IpAddress.fromBytesV6(be);
    }

    public byte[] getBytes() {
        return this.mIp;
    }

    public String toString() {
        if (this.mIp.length == 4) {
            return IpAddress.numericToTextFormatV4(this.mIp);
        }
        return IpAddress.numericToTextFormatV6(this.mIp);
    }

    public int compareTo(IpAddress anIp) {
        return Arrays.compareUnsigned(this.mIp, anIp.getBytes());
    }

    private static byte[] bsWap(byte[] b) {
        byte[] rev = new byte[b.length];
        int i = 0;
        for (int j = b.length - 1; j >= 0; --j) {
            rev[j] = b[i];
            ++i;
        }
        return rev;
    }

    private static byte[] textToNumericFormatV4(String src) {
        int len = src.length();
        if (len == 0 || len > 15) {
            return null;
        }
        if (src.indexOf(46) < 1) {
            return null;
        }
        byte[] res = new byte[4];
        long tmpValue = 0L;
        int currByte = 0;
        boolean newOctet = true;
        for (int i = 0; i < len; ++i) {
            char c = src.charAt(i);
            if (c == '.') {
                if (newOctet || tmpValue < 0L || tmpValue > 255L || currByte == 3) {
                    return null;
                }
                res[currByte++] = (byte)(tmpValue & 0xFFL);
                tmpValue = 0L;
                newOctet = true;
                continue;
            }
            int digit = Character.digit(c, 10);
            if (digit < 0) {
                return null;
            }
            tmpValue *= 10L;
            tmpValue += (long)digit;
            newOctet = false;
        }
        if (newOctet || tmpValue < 0L || tmpValue >= 1L << (4 - currByte) * 8) {
            return null;
        }
        switch (currByte) {
            case 0: {
                res[0] = (byte)(tmpValue >> 24 & 0xFFL);
            }
            case 1: {
                res[1] = (byte)(tmpValue >> 16 & 0xFFL);
            }
            case 2: {
                res[2] = (byte)(tmpValue >> 8 & 0xFFL);
            }
            case 3: {
                res[3] = (byte)(tmpValue & 0xFFL);
            }
        }
        return res;
    }

    private byte[] textToNumericFormatV6(String src) {
        if (src.length() < 2) {
            return null;
        }
        char[] srcb = src.toCharArray();
        byte[] dst = new byte[16];
        int srcbLength = srcb.length;
        int pc = src.indexOf(37);
        if (pc == srcbLength - 1) {
            return null;
        }
        if (pc != -1) {
            srcbLength = pc;
        }
        int colonp = -1;
        int i = 0;
        int j = 0;
        if (srcb[i] == ':' && srcb[++i] != ':') {
            return null;
        }
        int curtok = i;
        boolean sawXdigit = false;
        int val = 0;
        while (i < srcbLength) {
            char ch;
            int chval;
            if ((chval = Character.digit(ch = srcb[i++], 16)) != -1) {
                val <<= 4;
                if ((val |= chval) > 65535) {
                    return null;
                }
                sawXdigit = true;
                continue;
            }
            if (ch == ':') {
                curtok = i;
                if (!sawXdigit) {
                    if (colonp != -1) {
                        return null;
                    }
                    colonp = j;
                    continue;
                }
                if (i == srcbLength) {
                    return null;
                }
                if (j + 2 > 16) {
                    return null;
                }
                dst[j++] = (byte)(val >> 8 & 0xFF);
                dst[j++] = (byte)(val & 0xFF);
                sawXdigit = false;
                val = 0;
                continue;
            }
            if (ch == '.' && j + 4 <= 16) {
                String ia4 = src.substring(curtok, srcbLength);
                int dotCount = 0;
                int index = 0;
                while ((index = ia4.indexOf(46, index)) != -1) {
                    ++dotCount;
                    ++index;
                }
                if (dotCount != 3) {
                    return null;
                }
                byte[] v4Addr = IpAddress.textToNumericFormatV4(ia4);
                if (v4Addr == null) {
                    return null;
                }
                for (int k = 0; k < 4; ++k) {
                    dst[j++] = v4Addr[k];
                }
                sawXdigit = false;
                break;
            }
            return null;
        }
        if (sawXdigit) {
            if (j + 2 > 16) {
                return null;
            }
            dst[j++] = (byte)(val >> 8 & 0xFF);
            dst[j++] = (byte)(val & 0xFF);
        }
        if (colonp != -1) {
            int n = j - colonp;
            if (j == 16) {
                return null;
            }
            for (i = 1; i <= n; ++i) {
                dst[16 - i] = dst[colonp + n - i];
                dst[colonp + n - i] = 0;
            }
            j = 16;
        }
        if (j != 16) {
            return null;
        }
        this.mIp = dst;
        return this.mIp;
    }

    private static String numericToTextFormatV4(byte[] src) {
        return (src[0] & 0xFF) + "." + (src[1] & 0xFF) + "." + (src[2] & 0xFF) + "." + (src[3] & 0xFF);
    }

    private static String numericToTextFormatV6(byte[] src) {
        StringBuilder sb = new StringBuilder(39);
        for (int i = 0; i < 8; ++i) {
            sb.append(Integer.toHexString(src[i << 1] << 8 & 0xFF00 | src[(i << 1) + 1] & 0xFF));
            if (i >= 7) continue;
            sb.append(':');
        }
        return sb.toString();
    }
}

