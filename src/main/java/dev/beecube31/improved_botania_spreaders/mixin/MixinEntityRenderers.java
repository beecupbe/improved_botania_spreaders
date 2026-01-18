package dev.beecube31.improved_botania_spreaders.mixin;

import dev.beecube31.improved_botania_spreaders.core.IBSElements;
import dev.beecube31.improved_botania_spreaders.render.ImprovedSpreaderRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.render.entity.EntityRenderers;

@Mixin(value = EntityRenderers.class, remap = false)
public class MixinEntityRenderers {
    @Inject(method = "registerBlockEntityRenderers", at = @At("TAIL"), remap = false)
    private static void mbp$registerOwnRenders(EntityRenderers.BERConsumer consumer, CallbackInfo ci) {
        consumer.register(IBSElements.NILFHEIM_SPREADER_TILE, ImprovedSpreaderRenderer::new);
        consumer.register(IBSElements.ALFHEIM_SPREADER_TILE, ImprovedSpreaderRenderer::new);
        consumer.register(IBSElements.ASGARD_SPREADER_TILE, ImprovedSpreaderRenderer::new);
        consumer.register(IBSElements.MUSPELHEIM_SPREADER_TILE, ImprovedSpreaderRenderer::new);
    }
}
