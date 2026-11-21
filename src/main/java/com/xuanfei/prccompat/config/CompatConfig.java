package com.xuanfei.prccompat.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Client config. The defaults are deliberately compatibility-first: Real Camera's
 * first-person body mesh is hidden so it cannot fight Punchy!'s first-person arms
 * or Punchy! Moves' body pose.
 */
public final class CompatConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue HIDE_REAL_CAMERA_BODY;
    public static final ModConfigSpec.BooleanValue ONLY_WHEN_PUNCHY;
    public static final ModConfigSpec.BooleanValue LOOSE_TEXTURE_MATCHING;
    public static final ModConfigSpec.BooleanValue LOG_BIND_DIAGNOSTICS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment(
                "Real Camera draws the whole player model in first person and cancels the vanilla first-person hands.",
                "That is what conflicts with Punchy! / Punchy! Moves. When this option is on, Real Camera keeps",
                "its camera binding but skips the visible first-person body pass. Its camera still follows the",
                "head pose that Punchy! Moves applies, and Punchy!'s hands are rendered normally."
        ).push("compat");

        HIDE_REAL_CAMERA_BODY = builder
                .comment("Hide Real Camera's first-person player model while Punchy! or Punchy! Moves is installed.")
                .define("hideRealCameraBody", true);

        ONLY_WHEN_PUNCHY = builder
                .comment("Only hide the body when the first-person Punchy! mod is present. When false (default),",
                        "Punchy! Moves alone is enough to trigger the fix.")
                .define("onlyWhenPunchyInstalled", false);

        LOOSE_TEXTURE_MATCHING = builder
                .comment("Real Camera only binds to a player head when the rendered skin texture ID matches one of",
                        "its configured targets (minecraft:textures/entity/player/ or minecraft:skins/).",
                        "CustomSkinLoader, Entity Texture Features and similar mods can use a different texture ID,",
                        "which makes automatic binding fail even though the head is rendered normally.",
                        "When this option is on, the compat layer retries the configured targets without the",
                        "texture-ID filter. The UV layout of a standard 64x64 player skin is unchanged, so the",
                        "head still binds to the animated Punchy! Moves pose instead of falling back to a fixed camera.")
                .define("looseTextureMatching", true);

        LOG_BIND_DIAGNOSTICS = builder
                .comment("Log the first few unmatched skin texture IDs. Useful when automatic binding still fails;",
                        "each unique texture ID is logged only once.")
                .define("logBindDiagnostics", true);

        builder.pop();
        SPEC = builder.build();
    }

    public static boolean hideRealCameraBody() {
        return !SPEC.isLoaded() || HIDE_REAL_CAMERA_BODY.get();
    }

    public static boolean onlyWhenPunchyInstalled() {
        return SPEC.isLoaded() && ONLY_WHEN_PUNCHY.get();
    }

    public static boolean looseTextureMatching() {
        return !SPEC.isLoaded() || LOOSE_TEXTURE_MATCHING.get();
    }

    public static boolean logBindDiagnostics() {
        return !SPEC.isLoaded() || LOG_BIND_DIAGNOSTICS.get();
    }

    private CompatConfig() {
    }
}
