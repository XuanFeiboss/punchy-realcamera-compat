package com.xuanfei.prccompat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reflective access to Real Camera's configured bind targets. Reflection keeps this
 * compat mod independent of Real Camera at compile time; the mixin that calls it is
 * only applied when Real Camera is present.
 */
public final class BindTargetFallback {
    private static final Logger LOGGER = LoggerFactory.getLogger("prccompat");
    private static final Set<String> LOGGED_TEXTURES = ConcurrentHashMap.newKeySet();
    private static final int MAX_LOGGED_TEXTURES = 8;

    private static Field bindingField;
    private static Field targetListField;

    /**
     * Returns Real Camera's full configured target list, ignoring its texture-ID filter.
     */
    public static List<?> configuredTargets(Object modConfig) {
        try {
            if (bindingField == null) {
                bindingField = modConfig.getClass().getField("binding");
                Object binding = bindingField.get(modConfig);
                targetListField = binding.getClass().getField("targetList");
            }
            Object binding = bindingField.get(modConfig);
            Object targets = targetListField.get(binding);
            if (targets instanceof List<?> list) {
                return list;
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            LOGGER.warn("[Punchy x Real Camera] Could not read Real Camera's bind target list", e);
        }
        return List.of();
    }

    /**
     * Logs each unmatched skin texture ID once, capped to avoid filling the log.
     */
    public static void logUnmatched(String textureId) {
        if (!ModPresence.shouldLogBindDiagnostics()) {
            return;
        }
        if (LOGGED_TEXTURES.size() >= MAX_LOGGED_TEXTURES || !LOGGED_TEXTURES.add(textureId)) {
            return;
        }
        LOGGER.info("[Punchy x Real Camera] No configured bind target matches skin texture '{}' (looseTextureMatching={})",
                textureId, com.xuanfei.prccompat.config.CompatConfig.looseTextureMatching());
    }

    private BindTargetFallback() {
    }
}
