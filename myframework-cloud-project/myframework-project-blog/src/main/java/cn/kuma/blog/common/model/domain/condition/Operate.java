package cn.kuma.blog.common.model.domain.condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum Operate {

    /**
     * 且.
     */
    AND("AND"),
    /**
     * 或.
     */
    OR("OR");

    private final String displayName;

    private static final Map<String, Operate> DISPLAY_NAME_ENUM_CACHES = Arrays.stream(Operate.values())
        .collect(Collectors.toMap(Operate::getDisplayName, item -> item));

    Operate(String displayName) {
        this.displayName = displayName;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Operate fromString(String displayName) {
        return DISPLAY_NAME_ENUM_CACHES.getOrDefault(displayName.toUpperCase(Locale.ENGLISH), AND);
    }

}
