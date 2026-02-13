/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.system;

import java.io.IOException;
import java.net.ServerSocket;

public final class OsUtils {
    private static final String ARCH = System.getProperty("sun.arch.data.model");
    private static final String OS = System.getProperty("os.name").toLowerCase();

    private OsUtils() {
    }

    public static int getFreePort(int defaultPort) throws IOException {
        int n;
        ServerSocket serverSocket = new ServerSocket(defaultPort);
        try {
            n = serverSocket.getLocalPort();
        }
        catch (Throwable throwable) {
            try {
                try {
                    serverSocket.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                return OsUtils.getFreePort();
            }
        }
        serverSocket.close();
        return n;
    }

    public static int getFreePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0);){
            int n = serverSocket.getLocalPort();
            return n;
        }
    }

    public static boolean isBusyPort(int port) {
        boolean bl;
        boolean ret = true;
        ServerSocket serverSocket = new ServerSocket(port);
        try {
            bl = true;
        }
        catch (Throwable throwable) {
            try {
                try {
                    serverSocket.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception e) {
                return false;
            }
        }
        serverSocket.close();
        return bl;
    }

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isWindowsXP() {
        return OS.contains("win") && OS.contains("xp");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    public static boolean isSolaris() {
        return OS.contains("sunos");
    }

    public static boolean is64() {
        return "64".equals(ARCH);
    }

    public static boolean is32() {
        return "32".equals(ARCH);
    }
}

