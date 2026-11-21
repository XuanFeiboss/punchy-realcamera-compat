package com.xuanfei.prccompat.mixin;

import com.xuanfei.prccompat.BindTargetFallback;
import com.xuanfei.prccompat.ModPresence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Real Camera selects bind targets by checking whether the rendered texture ID contains
 * one of the configured target IDs. CustomSkinLoader / Entity Texture Features can expose
 * a different texture ID for the same standard player skin, leaving the match list empty
 * even though the head vertices were captured successfully.
 *
 * This mixin only relaxes that texture-ID filter. The UV matching in
 * {@code RealCameraCore.computeBindResult} is still performed, so the camera continues to
 * follow the real rendered head (including Punchy! Moves' animated pose).
 */
@Mixin(targets = "com.xtracr.realcamera.config.ModConfig", remap = false)
public abstract class ModConfigMixin {

    @Inject(method = "getBindTargetList", at = @At("RETURN"), cancellable = true, remap = false)
    private void prccompat$looseTextureMatch(String textureId, CallbackInfoReturnable<List> cir) {
        List<?> matched = cir.getReturnValue();
        if (matched != null && !matched.isEmpty()) {
            return;
        }

        BindTargetFallback.logUnmatched(textureId);

        if (!ModPresence.shouldLooseMatchBindTargets()) {
            return;
        }

        List<?> configured = BindTargetFallback.configuredTargets(this);
        if (!configured.isEmpty()) {
            cir.setReturnValue(configured);
        }
    }
}
