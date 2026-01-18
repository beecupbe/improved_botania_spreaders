package dev.beecube31.improved_botania_spreaders.block;

import dev.beecube31.improved_botania_spreaders.core.IBSConfig;
import dev.beecube31.improved_botania_spreaders.core.IBSElements;
import dev.beecube31.improved_botania_spreaders.tiles.TileImprovedSpreader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.mana.BasicLensItem;
import vazkii.botania.api.state.BotaniaStateProperties;
import vazkii.botania.common.block.BotaniaWaterloggedBlock;
import vazkii.botania.common.block.block_entity.mana.ManaSpreaderBlockEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.item.WandOfTheForestItem;

import java.util.function.Supplier;

public class BlockImprovedSpreader extends BotaniaWaterloggedBlock implements EntityBlock {
    public enum Variant {
        NILFHEIM(IBSConfig.COMMON.nilfheimSpreaderMaxMana, () -> IBSElements.NILFHEIM_SPREADER_TILE, IBSConfig.COMMON.nilfheimSpreaderManaPerBurst,
                0x7B54D2, 0x7B54D2),
        MUSPELHEIM(IBSConfig.COMMON.muspelheimSpreaderMaxMana, () -> IBSElements.MUSPELHEIM_SPREADER_TILE, IBSConfig.COMMON.muspelheimSpreaderManaPerBurst,
                0xFF85B9, 0xFF85B9),
        ALFHEIM(IBSConfig.COMMON.alfheimSpreaderMaxMana, () -> IBSElements.ALFHEIM_SPREADER_TILE, IBSConfig.COMMON.alfheimSpreaderManaPerBurst,
                0xDECFE4, 0xDECFE4),
        ASGARD(IBSConfig.COMMON.asgardSpreaderMaxMana, () -> IBSElements.ASGARD_SPREADER_TILE, IBSConfig.COMMON.asgardSpreaderManaPerBurst,
                0x3CB9E5, 0x3CB9E5);

        public final Supplier<BlockEntityType<TileImprovedSpreader>> associatedTile;

        public final ForgeConfigSpec.IntValue burstMana;
        public final ForgeConfigSpec.IntValue manaCapacity;
        public final int color;
        public final int hudColor;
        public final int preLossTicks = Integer.MAX_VALUE;
        public final float lossPerTick = 0f;
        public final float motionModifier = 2.5f;

        Variant(ForgeConfigSpec.IntValue manaCapacity, Supplier<BlockEntityType<TileImprovedSpreader>> associatedTile, ForgeConfigSpec.IntValue burstMana,
                int color, int hudColor) {
            this.associatedTile = associatedTile;
            this.burstMana = burstMana;
            this.manaCapacity = manaCapacity;
            this.color = color;
            this.hudColor = hudColor;
        }
    }

    private static final VoxelShape SHAPE = box(2, 2, 2, 14, 14, 14);
    private static final VoxelShape SHAPE_PADDING = box(1, 1, 1, 15, 15, 15);
    private static final VoxelShape SHAPE_SCAFFOLDING = box(0, 0, 0, 16, 16, 16);
    public final Variant variant;

    public BlockImprovedSpreader(Variant v, BlockBehaviour.Properties builder) {
        super(builder);
        this.registerDefaultState(this.defaultBlockState().setValue(BotaniaStateProperties.HAS_SCAFFOLDING, false));
        this.variant = v;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BotaniaStateProperties.HAS_SCAFFOLDING);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState blockState, @NotNull BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if (blockState.getValue(BotaniaStateProperties.HAS_SCAFFOLDING)) {
            return SHAPE_SCAFFOLDING;
        }
        BlockEntity be = blockGetter.getBlockEntity(blockPos);
        return be instanceof ManaSpreaderBlockEntity spreader && spreader.paddingColor != null ? SHAPE_PADDING : SHAPE;
    }

    @NotNull
    public VoxelShape getOcclusionShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return SHAPE;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Direction orientation = placer == null ? Direction.WEST : Direction.orderedByNearest(placer)[0].getOpposite();
        ManaSpreaderBlockEntity spreader = (ManaSpreaderBlockEntity)world.getBlockEntity(pos);
        switch (orientation) {
            case DOWN:
                spreader.rotationY = -90.0F;
                break;
            case UP:
                spreader.rotationY = 90.0F;
                break;
            case NORTH:
                spreader.rotationX = 270.0F;
                break;
            case SOUTH:
                spreader.rotationX = 90.0F;
            case WEST:
            default:
                break;
            case EAST:
                spreader.rotationX = 180.0F;
        }
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (!(tile instanceof ManaSpreaderBlockEntity spreader)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.getItem() instanceof WandOfTheForestItem) {
            return InteractionResult.PASS;
        }
        boolean mainHandEmpty = player.getMainHandItem().isEmpty();

        ItemStack lens = spreader.getItemHandler().getItem(0);
        boolean playerHasLens = heldItem.getItem() instanceof BasicLensItem;
        boolean lensIsSame = playerHasLens && ItemStack.isSameItemSameTags(heldItem, lens);
        ItemStack wool = spreader.paddingColor != null
                ? new ItemStack(ColorHelper.WOOL_MAP.apply(spreader.paddingColor))
                : ItemStack.EMPTY;
        boolean playerHasWool = ColorHelper.isWool(Block.byItem(heldItem.getItem()));
        boolean woolIsSame = playerHasWool && ItemStack.isSameItemSameTags(heldItem, wool);
        boolean playerHasScaffolding = !heldItem.isEmpty() && heldItem.is(Items.SCAFFOLDING);
        boolean shouldInsert = (playerHasLens && !lensIsSame)
                || (playerHasWool && !woolIsSame)
                || (playerHasScaffolding && !state.getValue(BotaniaStateProperties.HAS_SCAFFOLDING));

        if (shouldInsert) {
            if (playerHasLens) {
                ItemStack toInsert = heldItem.split(1);

                if (!lens.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(lens);
                }

                spreader.getItemHandler().setItem(0, toInsert);
                world.playSound(player, pos, BotaniaSounds.spreaderAddLens, SoundSource.BLOCKS, 1F, 1F);
            } else if (playerHasWool) {
                Block woolBlock = Block.byItem(heldItem.getItem());

                heldItem.shrink(1);
                if (spreader.paddingColor != null) {
                    ItemStack spreaderWool = new ItemStack(ColorHelper.WOOL_MAP.apply(spreader.paddingColor));
                    player.getInventory().placeItemBackInInventory(spreaderWool);
                }

                spreader.paddingColor = ColorHelper.getWoolColor(woolBlock);
                spreader.setChanged();
                world.playSound(player, pos, BotaniaSounds.spreaderCover, SoundSource.BLOCKS, 1F, 1F);
            } else {
                world.setBlockAndUpdate(pos, state.setValue(BotaniaStateProperties.HAS_SCAFFOLDING, true));
                world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                world.playSound(player, pos, BotaniaSounds.spreaderScaffold, SoundSource.BLOCKS, 1F, 1F);
            }
            return InteractionResult.sidedSuccess(world.isClientSide());
        }

        if (state.getValue(BotaniaStateProperties.HAS_SCAFFOLDING) && player.isSecondaryUseActive()) {
            if (!player.getAbilities().instabuild) {
                ItemStack scaffolding = new ItemStack(Items.SCAFFOLDING);
                player.getInventory().placeItemBackInInventory(scaffolding);
            }
            world.setBlockAndUpdate(pos, state.setValue(BotaniaStateProperties.HAS_SCAFFOLDING, false));
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));

            world.playSound(player, pos, BotaniaSounds.spreaderUnScaffold, SoundSource.BLOCKS, 1F, 1F);

            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        if (!lens.isEmpty() && (mainHandEmpty || lensIsSame)) {
            player.getInventory().placeItemBackInInventory(lens);
            spreader.getItemHandler().setItem(0, ItemStack.EMPTY);

            world.playSound(player, pos, BotaniaSounds.spreaderRemoveLens, SoundSource.BLOCKS, 1F, 1F);

            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        if (spreader.paddingColor != null && (mainHandEmpty || woolIsSame)) {
            player.getInventory().placeItemBackInInventory(wool);
            spreader.paddingColor = null;
            spreader.setChanged();

            world.playSound(player, pos, BotaniaSounds.spreaderUncover, SoundSource.BLOCKS, 1F, 1F);

            return InteractionResult.sidedSuccess(world.isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (!(tile instanceof ManaSpreaderBlockEntity spreader)) {
                return;
            }

            if (spreader.paddingColor != null) {
                ItemStack padding = new ItemStack(ColorHelper.WOOL_MAP.apply(spreader.paddingColor));
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), padding);
            }

            if (state.getValue(BotaniaStateProperties.HAS_SCAFFOLDING)) {
                ItemStack scaffolding = new ItemStack(Items.SCAFFOLDING);
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), scaffolding);
            }

            Containers.dropContents(world, pos, spreader.getItemHandler());

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    @NotNull
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileImprovedSpreader(pos, state, this.variant);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, this.variant.associatedTile.get(), TileImprovedSpreader::commonTick);
    }
}
