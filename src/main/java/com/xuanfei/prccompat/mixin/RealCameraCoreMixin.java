package com.xuanfei.prccompat.mixin;

import com.xuanfei.prccompat.ModPresence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Real Camera's {@code isRendering()} gates two things:
 * <ul>
 *     <li>the visible first-person player-model pass in {@code MixinLevelRenderer};</li>
 *     <li>the vanilla hand cancellation in {@code MixinItemInHandRenderer}.</li>
 * </ul>
 * Forcing it to false keeps Real Camera's camera binding (which only needs {@code isActive()}),
 * removes the duplicate body, and lets Punchy! draw its own first-person hands.
 */
@Mixin(targets = "com.xtracr.realcamera.RealCameraCore", remap = false)
public abstract class RealCameraCoreMixin {

    @Inject(method = "isRendering()Z", at = @At("HEAD"), cancellable = true, remap = false)
    private static void prccompat$hideFirstPersonBody(CallbackInfoReturnable<Boolean> cir) {
        if (ModPresence.shouldHideRealCameraBody()) {
            cir.setReturnValue(false);
        }
    }
}
