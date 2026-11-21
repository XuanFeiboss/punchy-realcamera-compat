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

        builder.pop();
        SPEC = builder.build();
    }

    public static boolean hideRealCameraBody() {
        return !SPEC.isLoaded() || HIDE_REAL_CAMERA_BODY.get();
    }

    public static boolean onlyWhenPunchyInstalled() {
        return SPEC.isLoaded() && ONLY_WHEN_PUNCHY.get();
    }

    private CompatConfig() {
    }
}
