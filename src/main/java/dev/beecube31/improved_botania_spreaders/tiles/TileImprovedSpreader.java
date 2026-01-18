package dev.beecube31.improved_botania_spreaders.tiles;

import com.google.common.base.Predicates;
import dev.beecube31.improved_botania_spreaders.block.BlockImprovedSpreader;
import dev.beecube31.improved_botania_spreaders.interfaces.IMixinSpreader;
import dev.beecube31.improved_botania_spreaders.mixin.AccessorTileManaSpreader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.*;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.mana.ManaSpreaderBlockEntity;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.handler.ManaNetworkHandler;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TileImprovedSpreader extends ManaSpreaderBlockEntity {
    public static class WandHud implements WandHUD {
        private final TileImprovedSpreader spreader;

        public WandHud(TileImprovedSpreader spreader) {
            this.spreader = spreader;
        }

        public void renderHUD(GuiGraphics gui, Minecraft mc) {
            String spreaderName = (new ItemStack(this.spreader.getBlockState().getBlock())).getHoverName().getString();
            ItemStack lensStack = this.spreader.getItemHandler().getItem(0);
            ItemStack recieverStack = ((AccessorTileManaSpreader) this.spreader).receiver() == null ? ItemStack.EMPTY :
                    new ItemStack(this.spreader.level.getBlockState(((AccessorTileManaSpreader) this.spreader).receiver().getManaReceiverPos()).getBlock());
            int width = 4 + Collections.max(Arrays.asList(102, mc.font.width(spreaderName), RenderHelper.itemWithNameWidth(mc, lensStack), RenderHelper.itemWithNameWidth(mc, recieverStack)));
            int height = 22 + (lensStack.isEmpty() ? 0 : 18) + (recieverStack.isEmpty() ? 0 : 18);
            int centerX = mc.getWindow().getGuiScaledWidth() / 2;
            int centerY = mc.getWindow().getGuiScaledHeight() / 2;
            RenderHelper.renderHUDBox(gui, centerX - width / 2, centerY + 8, centerX + width / 2, centerY + 8 + height);
            int color = this.spreader.getMyVariant().hudColor;
            BotaniaAPIClient.instance().drawSimpleManaHUD(gui, color, this.spreader.getCurrentMana(), this.spreader.getMaxMana(), spreaderName);
            RenderHelper.renderItemWithNameCentered(gui, mc, recieverStack, centerY + 30, color);
            RenderHelper.renderItemWithNameCentered(gui, mc, lensStack, centerY + (recieverStack.isEmpty() ? 30 : 48), color);
        }
    }

    private final BlockImprovedSpreader.Variant myVariant;

    public TileImprovedSpreader(BlockPos pos, BlockState state, BlockImprovedSpreader.Variant myVariant) {
        super(pos, state);

        this.myVariant = myVariant;
    }

    public static void commonTick(Level level, BlockPos worldPosition, BlockState state, TileImprovedSpreader self) {
        boolean wasInNetwork = ManaNetworkHandler.instance.isCollectorIn(level, self);
        if (!wasInNetwork && !self.isRemoved()) {
            BotaniaAPI.instance().getManaNetworkInstance().fireManaNetworkEvent(self, ManaBlockType.COLLECTOR, ManaNetworkAction.ADD);
        }

        boolean powered = false;

        for (Direction dir : Direction.values()) {
            var relPos = worldPosition.relative(dir);
            if (level.hasChunkAt(relPos)) {
                var receiverAt = XplatAbstractions.INSTANCE.findManaReceiver(level, relPos, dir.getOpposite());
                if (receiverAt instanceof ManaPool pool) {
                    if (wasInNetwork && (pool != ((AccessorTileManaSpreader) self).receiver())) {
                        if (pool instanceof KeyLocked locked && !locked.getOutputKey().equals(self.getInputKey())) {
                            continue;
                        }

                        int manaInPool = pool.getCurrentMana();
                        if (manaInPool > 0 && !self.isFull()) {
                            int manaMissing = self.getMaxMana() - ((AccessorTileManaSpreader) self).mana();
                            int manaToRemove = Math.min(manaInPool, manaMissing);
                            pool.receiveMana(-manaToRemove);
                            self.receiveMana(manaToRemove);
                        }
                    }
                }
                powered = powered || level.hasSignal(relPos, dir);
            }
        }

        if (((IMixinSpreader) self).ibs$needsNewBurstSimulation()) {
            self.checkForReceiver();
        }

        if (!self.canShootBurst) {
            if (self.pingbackTicks <= 0) {
                double x = self.lastPingbackX;
                double y = self.lastPingbackY;
                double z = self.lastPingbackZ;
                AABB aabb = new AABB(x, y, z, x, y, z).inflate(0.5, 0.5, 0.5);
                List<ManaBurst> bursts = (List<ManaBurst>) (List<?>) level.getEntitiesOfClass(ThrowableProjectile.class, aabb, Predicates.instanceOf(ManaBurst.class));
                ManaBurst found = null;
                UUID identity = self.getIdentifier();
                for (ManaBurst burst : bursts) {
                    if (burst != null && identity.equals(burst.getShooterUUID())) {
                        found = burst;
                        break;
                    }
                }

                if (found != null) {
                    found.ping();
                } else {
                    self.setCanShoot(true);
                }
            } else {
                self.pingbackTicks--;
            }
        }

        boolean shouldShoot = !powered;

        if (shouldShoot && ((AccessorTileManaSpreader) self).receiver() instanceof KeyLocked locked) {
            shouldShoot = locked.getInputKey().equals(self.getOutputKey());
        }

        ItemStack lens = self.getItemHandler().getItem(0);
        ControlLensItem control = self.getLensController(lens);
        if (control != null) {
            control.onControlledSpreaderTick(lens, self, powered);

            shouldShoot = shouldShoot && control.allowBurstShooting(lens, self, powered);
        }

        if (shouldShoot) {
            self.tryShootBurst();
        }

        if (((AccessorTileManaSpreader) self).receiverLastTick() != ((AccessorTileManaSpreader) self).receiver() && !level.isClientSide) {
            ((IMixinSpreader) self).ibs$setRequestsClientUpdate(true);
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self);
        }

        ((IMixinSpreader) self).ibs$setPoweredLastTick(powered);
        ((IMixinSpreader) self).ibs$setReceiverLastTick(((AccessorTileManaSpreader) self).receiver());
    }

    private void tryShootBurst() {
        if (((AccessorTileManaSpreader) this).receiver() != null && !((AccessorTileManaSpreader) this).invalidTentativeBurst()
                && this.canShootBurst && (((AccessorTileManaSpreader) this).receiver().canReceiveManaFromBursts() && !((AccessorTileManaSpreader) this).receiver().isFull())) {
            ManaBurstEntity burst = this.getMyBurst(false);

            if (burst != null && !this.level.isClientSide) {
                this.receiveMana(-burst.getStartingMana());
                burst.setShooterUUID(this.getIdentifier());
                this.level.addFreshEntity(burst);
                burst.ping();
                if (!BotaniaConfig.common().silentSpreaders()) {
                    this.level.playSound(null, this.worldPosition, BotaniaSounds.spreaderFire, SoundSource.BLOCKS, 0.05F * (this.paddingColor != null ? 0.2F : 1.0F), 0.7F + 0.3F * (float)Math.random());
                }
            }
        }
    }

    private static NonNullList<ItemStack> copyFromInv(Container inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < inv.getContainerSize(); ++i) {
            ret.set(i, inv.getItem(i));
        }

        return ret;
    }

    public BlockImprovedSpreader.Variant getMyVariant() {
        return this.myVariant;
    }

    private ManaBurstEntity getMyBurst(boolean fake) {
        BlockImprovedSpreader.Variant variant = this.getMyVariant();
        float gravity = 0.0F;
        BurstProperties props = new BurstProperties(variant.burstMana.get(), variant.preLossTicks, variant.lossPerTick, gravity, variant.motionModifier, variant.color);
        ItemStack lens = this.getItemHandler().getItem(0);
        if (!lens.isEmpty()) {
            Item var7 = lens.getItem();
            if (var7 instanceof LensEffectItem lensEffectItem) {
                lensEffectItem.apply(lens, props, this.level);
            }
        }

        if (this.getCurrentMana() > 0 || fake) {
            ManaBurstEntity burst = new ManaBurstEntity(this.getLevel(), this.getBlockPos(), this.getRotationX(), this.getRotationY(), fake);
            burst.setSourceLens(lens);
            if (((AccessorTileManaSpreader) this).mapmakerOverride()) {
                burst.setColor(((AccessorTileManaSpreader) this).mmForcedColor());
                burst.setMana(((AccessorTileManaSpreader) this).mmForcedManaPayload());
                burst.setStartingMana(((AccessorTileManaSpreader) this).mmForcedManaPayload());
                burst.setMinManaLoss(((AccessorTileManaSpreader) this).mmForcedTicksBeforeManaLoss());
                burst.setManaLossPerTick(((AccessorTileManaSpreader) this).mmForcedManaLossPerTick());
                burst.setGravity(((AccessorTileManaSpreader) this).mmForcedGravity());
                burst.setDeltaMovement(burst.getDeltaMovement().scale(((AccessorTileManaSpreader) this).mmForcedVelocityMultiplier()));
            } else {
                burst.setColor(props.color);
                burst.setMana(Math.min(props.maxMana, this.getCurrentMana()));
                burst.setStartingMana(Math.min(props.maxMana, this.getCurrentMana()));
                burst.setMinManaLoss(props.ticksBeforeManaLoss);
                burst.setManaLossPerTick(props.manaLossPerTick);
                burst.setGravity(props.gravity);
                burst.setDeltaMovement(burst.getDeltaMovement().scale(props.motionModifier));
            }

            return burst;
        }

        return null;
    }

    @Override
    public int getMaxMana() {
        return this.myVariant.manaCapacity.get();
    }

    @Override
    public BlockEntityType<?> getType() {
        if (this.getBlockState().getBlock() instanceof BlockImprovedSpreader spreader) {
            return spreader.variant.associatedTile.get();
        }
        return BotaniaBlockEntities.POOL;
    }

    @Override
    public void checkForReceiver() {
        ItemStack stack = this.getItemHandler().getItem(0);
        ControlLensItem control = this.getLensController(stack);
        if (control == null || control.allowBurstShooting(stack, this, false)) {
            ManaBurstEntity fakeBurst = this.getMyBurst(true);
            fakeBurst.setScanBeam();
            ManaReceiver receiver = fakeBurst.getCollidedTile(true);
            if (receiver != null && receiver.getManaReceiverLevel().hasChunkAt(receiver.getManaReceiverPos())) {
                ((IMixinSpreader) this).ibs$setReceiver(receiver);
            } else {
                ((IMixinSpreader) this).ibs$setReceiver(null);
            }

            ((IMixinSpreader) this).ibs$setLastTentativeBurst(fakeBurst.propsList);
        }
    }

    @Override
    public ManaBurst runBurstSimulation() {
        ManaBurstEntity fakeBurst = this.getMyBurst(true);
        fakeBurst.setScanBeam();
        fakeBurst.getCollidedTile(true);
        return fakeBurst;
    }

    @Override
    public void onClientDisplayTick() {
        if (this.level != null) {
            ManaBurstEntity burst = this.getMyBurst(true);
            burst.getCollidedTile(false);
        }
    }

    @Override
    public void writePacketNBT(CompoundTag cmp) {
        ContainerHelper.saveAllItems(cmp, copyFromInv(this.getItemHandler()));
        cmp.putUUID("uuid", this.getIdentifier());
        cmp.putInt("mana", ((AccessorTileManaSpreader) this).mana());
        cmp.putFloat("rotationX", this.rotationX);
        cmp.putFloat("rotationY", this.rotationY);
        cmp.putBoolean("requestUpdate", ((AccessorTileManaSpreader) this).requestsClientUpdate());
        cmp.putInt("paddingColor", this.paddingColor == null ? -1 : this.paddingColor.getId());
        cmp.putBoolean("canShootBurst", this.canShootBurst);
        cmp.putInt("pingbackTicks", this.pingbackTicks);
        cmp.putDouble("lastPingbackX", this.lastPingbackX);
        cmp.putDouble("lastPingbackY", this.lastPingbackY);
        cmp.putDouble("lastPingbackZ", this.lastPingbackZ);
        cmp.putString("inputKey", ((AccessorTileManaSpreader) this).inputKey());
        cmp.putString("outputKey", "");
        cmp.putInt("forceClientBindingX", ((AccessorTileManaSpreader) this).receiver() == null ? 0 : ((AccessorTileManaSpreader) this).receiver().getManaReceiverPos().getX());
        cmp.putInt("forceClientBindingY", ((AccessorTileManaSpreader) this).receiver() == null ? Integer.MIN_VALUE : ((AccessorTileManaSpreader) this).receiver().getManaReceiverPos().getY());
        cmp.putInt("forceClientBindingZ", ((AccessorTileManaSpreader) this).receiver() == null ? 0 : ((AccessorTileManaSpreader) this).receiver().getManaReceiverPos().getZ());
        cmp.putBoolean("mapmakerOverrideEnabled", ((AccessorTileManaSpreader) this).mapmakerOverride());
        cmp.putInt("mmForcedColor", ((AccessorTileManaSpreader) this).mmForcedColor());
        cmp.putInt("mmForcedManaPayload", ((AccessorTileManaSpreader) this).mmForcedManaPayload());
        cmp.putInt("mmForcedTicksBeforeManaLoss", ((AccessorTileManaSpreader) this).mmForcedTicksBeforeManaLoss());
        cmp.putFloat("mmForcedManaLossPerTick", ((AccessorTileManaSpreader) this).mmForcedManaLossPerTick());
        cmp.putFloat("mmForcedGravity", ((AccessorTileManaSpreader) this).mmForcedGravity());
        cmp.putFloat("mmForcedVelocityMultiplier", ((AccessorTileManaSpreader) this).mmForcedVelocityMultiplier());
        ((IMixinSpreader) this).ibs$setNoRequestsClientUpdate();
    }
}
