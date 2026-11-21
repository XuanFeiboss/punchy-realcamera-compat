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

    private static boolean active() {
        refresh();
        return CompatConfig.onlyWhenPunchyInstalled() ? punchy : punchy || punchyMoves;
    }

    /**
     * Called from the Real Camera mixin. Returning true means "pretend Real Camera is not rendering the body".
     */
    public static boolean shouldHideRealCameraBody() {
        if (!CompatConfig.hideRealCameraBody()) {
            return false;
        }

        boolean active = active();
        if (active && !logged) {
            logged = true;
            LOGGER.info("[Punchy x Real Camera] Hiding Real Camera's first-person body mesh (Punchy={}, Moves={})",
                    punchy, punchyMoves);
        }
        return active;
    }

    /**
     * Real Camera's binding target list is filtered by the skin texture ID. Custom skin
     * mods change that ID, so an otherwise valid player-head mesh silently produces no
     * bind result. This opt-in loosens only the texture-ID filter, not the UV matching.
     */
    public static boolean shouldLooseMatchBindTargets() {
        return CompatConfig.looseTextureMatching() && anyPunchyLoaded();
    }

    public static boolean shouldLogBindDiagnostics() {
        return CompatConfig.logBindDiagnostics() && anyPunchyLoaded();
    }

    private static boolean anyPunchyLoaded() {
        refresh();
        return punchy || punchyMoves;
    }

    private ModPresence() {
    }
}
