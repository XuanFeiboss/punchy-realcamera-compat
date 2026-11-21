package com.xuanfei.prccompat.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Only apply the Real Camera mixin when Real Camera is actually on the classpath.
 * The check uses a resource lookup instead of Class.forName so the target class is
 * not defined before Mixin has a chance to transform it.
 */
public final class PrcMixinPlugin implements IMixinConfigPlugin {

    private static final String REAL_CAMERA_CORE_RESOURCE = "com/xtracr/realcamera/RealCameraCore.class";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return PrcMixinPlugin.class.getClassLoader().getResource(REAL_CAMERA_CORE_RESOURCE) != null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
