package com.xuanfei.prccompat;

import com.xuanfei.prccompat.config.CompatConfig;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mod presence checks are cached because they are queried from a render hot path.
 */
public final class ModPresence {
    private static final Logger LOGGER = LoggerFactory.getLogger("prccompat");

    private static boolean punchy;
    private static boolean punchyMoves;
    private static boolean cached;
    private static boolean logged;

    private static void refresh() {
        if (cached) {
            return;
        }
        try {
            ModList list = ModList.get();
            if (list == null) {
                // Rendering can in theory run before the mod list is published; retry next frame.
                return;
            }
            punchy = list.isLoaded("punchy");
            punchyMoves = list.isLoaded("punchy_moves");
        } catch (Throwable ignored) {
            punchy = false;
            punchyMoves = false;
        }
        cached = true;
    }

    public static boolean punchyLoaded() {
        refresh();
        return punchy;
    }

    public static boolean punchyMovesLoaded() {
        refresh();
        return punchyMoves;
    }

    /**
     * Called from the Real Camera mixin. Returning true means "pretend Real Camera is not rendering the body".
     */
    public static boolean shouldHideRealCameraBody() {
        if (!CompatConfig.hideRealCameraBody()) {
            return false;
        }

        refresh();
        boolean active = CompatConfig.onlyWhenPunchyInstalled() ? punchy : punchy || punchyMoves;
        if (active && !logged) {
            logged = true;
            LOGGER.info("[Punchy x Real Camera] Hiding Real Camera's first-person body mesh (Punchy={}, Moves={})",
                    punchy, punchyMoves);
        }
        return active;
    }

    private ModPresence() {
    }
}
