package com.kuma.boot.common.enums;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum LevelEnum {
    HIGN(3, "极其严重"),
    MIDDLE(2, "严重"),
    LOW(1, "一般");

    private final int level;
    private final String description;

    LevelEnum(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() { return level; }
    public String getDescription() { return description; }

    private static final Map<Integer, LevelEnum> valueLookup = new ConcurrentHashMap<>(values().length);

    static {
        for (LevelEnum type : EnumSet.allOf(LevelEnum.class)) {
            valueLookup.put(type.level, type);
        }
    }

    public static LevelEnum resolve(Integer code) {
        return code != null ? valueLookup.get(code) : null;
    }

    public static String resolveName(Integer code) {
        LevelEnum mode = resolve(code);
        return mode == null ? "" : mode.getDescription();
    }
}
