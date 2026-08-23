package com.kuma.cloud.lab.starter.support;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.cloud.lab.starter.domain.vo.StarterCatalogItemVO;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 从 {@link StarterNameConstants} 加载 Starter 目录。
 */
public final class StarterCatalogLoader {

    private StarterCatalogLoader() {
    }

    public static List<StarterCatalogItemVO> load() {
        List<StarterCatalogItemVO> items = new ArrayList<>();
        for (Field field : StarterNameConstants.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || field.getType() != String.class) {
                continue;
            }
            if (!field.getName().endsWith("_STARTER")) {
                continue;
            }
            try {
                String starterName = (String) field.get(null);
                items.add(toItem(starterName));
            } catch (IllegalAccessException ignored) {
                // skip unreadable constant
            }
        }
        items.sort(Comparator.comparing(StarterCatalogItemVO::category)
                .thenComparing(StarterCatalogItemVO::name));
        return items;
    }

    private static StarterCatalogItemVO toItem(String starterName) {
        String category = StarterAnchorRegistry.categoryOf(starterName);
        return StarterAnchorRegistry.anchorClass(starterName)
                .map(anchor -> new StarterCatalogItemVO(
                        starterName,
                        category,
                        StarterAnchorRegistry.isClassPresent(anchor),
                        anchor
                ))
                .orElseGet(() -> new StarterCatalogItemVO(
                        starterName,
                        category,
                        null,
                        null
                ));
    }

}
