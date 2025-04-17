package icu.takeneko.appwebterminal.mixins;

import icu.takeneko.appwebterminal.client.all.ShadersKt;
import icu.takeneko.appwebterminal.client.rendering.foundation.PostProcess;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "resize", at = @At("HEAD"))
    private void onResize(int width, int height, CallbackInfo ci) {
        PostProcess instance = ShadersKt.getBlurPostProcessInstance();
        if (instance != null) {
            instance.resize(width, height);
        }
    }
}
