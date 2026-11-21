package com.xuanfei.prccompat;

import com.xuanfei.prccompat.config.CompatConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * Client-side compatibility shim between Real Camera and Punchy! / Punchy! Moves.
 */
@Mod(value = PunchyRealCameraCompat.MOD_ID, dist = Dist.CLIENT)
public final class PunchyRealCameraCompat {
    public static final String MOD_ID = "prccompat";

    public PunchyRealCameraCompat(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, CompatConfig.SPEC);
    }
}
