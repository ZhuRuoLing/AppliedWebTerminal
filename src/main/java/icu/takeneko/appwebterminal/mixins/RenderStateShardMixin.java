package icu.takeneko.appwebterminal.mixins;

import icu.takeneko.appwebterminal.client.rendering.AEKeyRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderStateShard.class)
public class RenderStateShardMixin {
    @Inject(
        method = "setupRenderState",
        at = @At("HEAD"),
        cancellable = true
    )
    private void invalidateOutputStateShardSetup(CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object) this instanceof RenderStateShard.OutputStateShard && AEKeyRenderer.Companion.getRendering()) {
            ci.cancel();
        }
    }

    @Inject(
        method = "clearRenderState",
        at = @At("HEAD"),
        cancellable = true
    )
    private void invalidateOutputStateShardClear(CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object) this instanceof RenderStateShard.OutputStateShard && AEKeyRenderer.Companion.getRendering()) {
            ci.cancel();
        }
    }
}
