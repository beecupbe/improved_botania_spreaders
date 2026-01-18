package dev.beecube31.improved_botania_spreaders.core;

import dev.beecube31.improved_botania_spreaders.block.BlockImprovedSpreader;
import dev.beecube31.improved_botania_spreaders.item.ItemImprovedSpreader;
import dev.beecube31.improved_botania_spreaders.tiles.TileImprovedSpreader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public abstract class IBSElements {
    private static final Item.Properties DEFAULT_ITEM_BUILDER = BotaniaItems.defaultBuilder();


    public static final Map<ResourceLocation, BlockEntityType<?>> ALL = new HashMap<>();

    public static final Block NILFHEIM_SPREADER = new BlockImprovedSpreader(BlockImprovedSpreader.Variant.NILFHEIM, BlockBehaviour.Properties.copy(BotaniaBlocks.livingrock)
            .isValidSpawn((state, world, pos, et) -> false));

    public static final Block MUSPELHEIM_SPREADER = new BlockImprovedSpreader(BlockImprovedSpreader.Variant.MUSPELHEIM, BlockBehaviour.Properties.copy(BotaniaBlocks.livingrock)
            .isValidSpawn((state, world, pos, et) -> false));

    public static final Block ALFHEIM_SPREADER = new BlockImprovedSpreader(BlockImprovedSpreader.Variant.ALFHEIM, BlockBehaviour.Properties.copy(BotaniaBlocks.livingrock)
            .isValidSpawn((state, world, pos, et) -> false));

    public static final Block ASGARD_SPREADER = new BlockImprovedSpreader(BlockImprovedSpreader.Variant.ASGARD, BlockBehaviour.Properties.copy(BotaniaBlocks.livingrock)
            .isValidSpawn((state, world, pos, et) -> false));

    public static final BlockItem NILFHEIM_SPREADER_ITEM = new ItemImprovedSpreader(NILFHEIM_SPREADER, DEFAULT_ITEM_BUILDER);
    public static final BlockItem MUSPELHEIM_SPREADER_ITEM = new ItemImprovedSpreader(MUSPELHEIM_SPREADER, DEFAULT_ITEM_BUILDER);
    public static final BlockItem ALFHEIM_SPREADER_ITEM = new ItemImprovedSpreader(ALFHEIM_SPREADER, DEFAULT_ITEM_BUILDER);
    public static final BlockItem ASGARD_SPREADER_ITEM = new ItemImprovedSpreader(ASGARD_SPREADER, DEFAULT_ITEM_BUILDER);


    public static final BlockEntityType<TileImprovedSpreader> NILFHEIM_SPREADER_TILE;
    public static final BlockEntityType<TileImprovedSpreader> MUSPELHEIM_SPREADER_TILE;
    public static final BlockEntityType<TileImprovedSpreader> ALFHEIM_SPREADER_TILE;
    public static final BlockEntityType<TileImprovedSpreader> ASGARD_SPREADER_TILE;

    public static void registerOwnBlocks(BiConsumer<Block, ResourceLocation> r) {
        r.accept(NILFHEIM_SPREADER, prefix("nilfheim_spreader"));
        r.accept(MUSPELHEIM_SPREADER, prefix("muspelheim_spreader"));
        r.accept(ALFHEIM_SPREADER, prefix("alfheim_spreader"));
        r.accept(ASGARD_SPREADER, prefix("asgard_spreader"));
    }

    public static void registerOwnItemBlocks(BiConsumer<Item, ResourceLocation> r) {
        r.accept(NILFHEIM_SPREADER_ITEM, BuiltInRegistries.BLOCK.getKey(NILFHEIM_SPREADER));
        r.accept(MUSPELHEIM_SPREADER_ITEM, BuiltInRegistries.BLOCK.getKey(MUSPELHEIM_SPREADER));
        r.accept(ALFHEIM_SPREADER_ITEM, BuiltInRegistries.BLOCK.getKey(ALFHEIM_SPREADER));
        r.accept(ASGARD_SPREADER_ITEM, BuiltInRegistries.BLOCK.getKey(ASGARD_SPREADER));
    }

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(IBSCore.MODID, path);
    }

    static {
        NILFHEIM_SPREADER_TILE = type(prefix("nilfheim_spreader_tile"), (pos, state) -> new TileImprovedSpreader(pos, state, BlockImprovedSpreader.Variant.NILFHEIM), NILFHEIM_SPREADER);
        MUSPELHEIM_SPREADER_TILE = type(prefix("muspelheim_spreader_tile"), (pos, state) -> new TileImprovedSpreader(pos, state, BlockImprovedSpreader.Variant.MUSPELHEIM), MUSPELHEIM_SPREADER);
        ALFHEIM_SPREADER_TILE = type(prefix("alfheim_spreader_tile"), (pos, state) -> new TileImprovedSpreader(pos, state, BlockImprovedSpreader.Variant.ALFHEIM), ALFHEIM_SPREADER);
        ASGARD_SPREADER_TILE = type(prefix("asgard_spreader_tile"), (pos, state) -> new TileImprovedSpreader(pos, state, BlockImprovedSpreader.Variant.ASGARD), ASGARD_SPREADER);

    }

    private static <T extends BlockEntity> BlockEntityType<T> type(ResourceLocation id, BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
        BlockEntityType<T> ret = XplatAbstractions.INSTANCE.createBlockEntityType(func, blocks);
        BlockEntityType<?> old = ALL.put(id, ret);
        if (old != null) {
            throw new IllegalArgumentException("Duplicate id " + id);
        } else {
            return ret;
        }
    }
}
