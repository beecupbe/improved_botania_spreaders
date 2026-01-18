package dev.beecube31.improved_botania_spreaders.core;

import dev.beecube31.improved_botania_spreaders.tiles.TileImprovedSpreader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.botania.api.block.WandHUD;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class IBSBlockTiles {
    public static void registerOwnTiles(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
        for (Map.Entry<ResourceLocation, BlockEntityType<?>> e : IBSElements.ALL.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    public static void registerWandHud(BotaniaBlockEntities.BECapConsumer<WandHUD> consumer) {
        consumer.accept((be) -> new TileImprovedSpreader.WandHud((TileImprovedSpreader) be), IBSElements.NILFHEIM_SPREADER_TILE);
        consumer.accept((be) -> new TileImprovedSpreader.WandHud((TileImprovedSpreader) be), IBSElements.ALFHEIM_SPREADER_TILE);
        consumer.accept((be) -> new TileImprovedSpreader.WandHud((TileImprovedSpreader) be), IBSElements.ASGARD_SPREADER_TILE);
        consumer.accept((be) -> new TileImprovedSpreader.WandHud((TileImprovedSpreader) be), IBSElements.MUSPELHEIM_SPREADER_TILE);
    }
}
