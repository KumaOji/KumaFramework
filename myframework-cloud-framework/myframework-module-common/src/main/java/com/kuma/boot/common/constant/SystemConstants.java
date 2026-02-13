/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.constant;

import java.util.List;
import java.util.Map;

public final class SystemConstants {
    public static final String VERSION = "java.version";
    public static final String VENDOR = "java.vendor";
    public static final String VENDOR_URL = "java.vendor.url";
    public static final String HOME = "java.home";
    public static final String VM_SPECIFICATION_VERSION = "java.vm.specification.version";
    public static final String VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";
    public static final String VM_SPECIFICATION_NAME = "java.vm.specification.name";
    public static final String VM_VERSION = "java.vm.version";
    public static final String VM_VENDOR = "java.vm.vendor";
    public static final String VM_NAME = "java.vm.name";
    public static final String SPECIFICATION_VERSION = "java.specification.version";
    public static final String SPECIFICATION_VENDOR = "java.specification.vendor";
    public static final String SPECIFICATION_NAME = "java.specification.name";
    public static final String CLASS_VERSION = "java.class.version";
    public static final String CLASS_PATH = "java.class.path";
    public static final String LIBRARY_PATH = "java.library.path";
    public static final String IO_TMPDIR = "java.io.tmpdir";
    public static final String COMPILER = "java.compiler";
    public static final String EXT_DIRS = "java.ext.dirs";
    public static final String OS_NAME = "os.name";
    public static final String OS_ARCH = "os.arch";
    public static final String OS_VERSION = "os.version";
    public static final String FILE_SEPARATOR = "file.separator";
    public static final String PATH_SEPARATOR = "path.separator";
    public static final String LINE_SEPARATOR = "line.separator";
    public static final String USER_NAME = "user.name";
    public static final String USER_HOME = "user.home";
    public static final String USER_DIR = "user.dir";
    public static final String KMC_RUNNING_IN_TEST = "kmc.running.in.test";

    private SystemConstants() {
    }

    public static class Dict {
        private Integer version;
        private Boolean boo;
        private List<Integer> it;
        private Map<String, Integer> st;

        public Integer getVersion() {
            return this.version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public Boolean getBoo() {
            return this.boo;
        }

        public void setBoo(Boolean boo) {
            this.boo = boo;
        }

        public List<Integer> getIt() {
            return this.it;
        }

        public void setIt(List<Integer> it) {
            this.it = it;
        }

        public Map<String, Integer> getSt() {
            return this.st;
        }

        public void setSt(Map<String, Integer> st) {
            this.st = st;
        }
    }
}

