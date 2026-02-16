package cn.kuma.blog.common.model.domain.condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum Relation {

    /**
     * equal to.
     */
    IN("="),
    /**
     * not equal to.
     */
    NOT_IN("!="),
    /**
     * greater than.
     */
    GT(">"),
    /**
     * less than.
     */
    LT("<"),
    /**
     * greater than or equal to.
     */
    GE(">="),
    /**
     * less than or equal to.
     */
    LE("<="),
    /**
     * like.
     */
    LIKE(":"),
    /**
     * not like.
     */
    NOT_LIKE("!:");

    private final String inputValue;

    private static final Map<String, Relation> DISPLAY_NAME_ENUM_CACHES = Arrays.stream(Relation.values())
        .collect(Collectors.toMap(Relation::getInputValue, (item) -> item));

    Relation(String inputValue) {
        this.inputValue = inputValue;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Relation fromString(String displayName) {
        return DISPLAY_NAME_ENUM_CACHES.getOrDefault(displayName.toUpperCase(Locale.ENGLISH), IN);
    }

}
