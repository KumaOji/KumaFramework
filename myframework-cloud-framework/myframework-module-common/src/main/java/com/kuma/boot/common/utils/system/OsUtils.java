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

package com.kuma.boot.common.utils.system;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 系统工具类
 *
 * @author kuma
 * @version 2022.09
 * @since 2023-01-03 11:28:50
 */
public final class OsUtils {

    /**
     * os跑龙套
     * @return
     * @since 2023-01-03 11:28:50
     */
    private OsUtils() {}

    /** 拱 */
    private static final String ARCH = System.getProperty("sun.arch.data.model");

    /** 操作系统 */
    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * 查看指定的端口号是否空闲，若空闲则返回否则返回一个随机的空闲端口号
     * @param defaultPort 默认端口
     * @return int
     * @since 2023-01-03 11:28:50
     */
    public static int getFreePort(int defaultPort) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(defaultPort)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            return getFreePort();
        }
    }

    /**
     * 获取空闲端口号
     * @return int
     * @since 2023-01-03 11:28:50
     */
    public static int getFreePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }

    /**
     * 检查端口号是否被占用
     * @param port 端口号
     * @return boolean
     * @since 2023-01-03 11:28:50
     */
    public static boolean isBusyPort(int port) {
        boolean ret = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否为 windows
     * @return boolean
     * @since 2023-01-03 11:28:50
     */
    public static boolean isWindows() {
        return OS.contains("win");
    }

    /**
     * 是否为 windows xp
     * @return boolean
     * @since 2023-01-03 11:28:50
     */
    public static boolean isWindowsXP() {
        return OS.contains("win") && OS.contains("xp");
    }

    /**
     * 是否为 mac
     * @return boolean
     * @since 2023-01-03 11:28:50
     */
    public static boolean isMac() {
        return OS.contains("mac");
    }

    /**
     * 是否为 unix
     * @return boolean
     * @since 2023-01-03 11:28:50
     */
    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    /**
     * 是否为 sunos
     * @return boolean
     * @since 2023-01-03 11:28:50
     */
    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

    /**
     * 是否为 64 位
     * @return boolean
     * @since 2023-01-03 11:28:50
     */
    public static boolean is64() {
        return "64".equals(ARCH);
    }

    /**
     * 是否为 32 位
     * @return boolean
     * @since 2023-01-03 11:28:50
     */
    public static boolean is32() {
        return "32".equals(ARCH);
    }
}
