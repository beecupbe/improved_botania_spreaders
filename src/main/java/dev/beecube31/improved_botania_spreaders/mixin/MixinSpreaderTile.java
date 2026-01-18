package dev.beecube31.improved_botania_spreaders.mixin;

import dev.beecube31.improved_botania_spreaders.interfaces.IMixinSpreader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.common.block.block_entity.ExposedSimpleInventoryBlockEntity;
import vazkii.botania.common.block.block_entity.mana.ManaSpreaderBlockEntity;
import vazkii.botania.common.entity.ManaBurstEntity;

import java.util.List;

@Mixin(ManaSpreaderBlockEntity.class)
public abstract class MixinSpreaderTile extends ExposedSimpleInventoryBlockEntity implements IMixinSpreader {

    protected MixinSpreaderTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    private boolean requestsClientUpdate;

    @Shadow
    private boolean hasReceivedInitialPacket;

    @Shadow
    private List<ManaBurstEntity.PositionProperties> lastTentativeBurst;

    @Shadow
    private boolean invalidTentativeBurst;

    @Shadow
    private boolean poweredLastTick;

    @Shadow
    private ManaReceiver receiverLastTick;

    @Shadow
    private ManaReceiver receiver;

    @Override
    public void ibs$setNoRequestsClientUpdate() {
        this.requestsClientUpdate = false;
    }

    @Override
    public void ibs$setPoweredLastTick(boolean powered) {
        this.poweredLastTick = powered;
    }

    @Override
    public void ibs$setReceiverLastTick(ManaReceiver receiver) {
        this.receiverLastTick = receiver;
    }

    @Override
    public void ibs$setRequestsClientUpdate(boolean req) {
        this.requestsClientUpdate = req;
    }

    @Override
    public boolean ibs$needsNewBurstSimulation() {
        if (this.level.isClientSide && !this.hasReceivedInitialPacket) {
            return false;
        } else if (this.lastTentativeBurst == null) {
            return true;
        } else {
            for(ManaBurstEntity.PositionProperties props : this.lastTentativeBurst) {
                if (!props.contentsEqual(this.level)) {
                    this.invalidTentativeBurst = props.isInvalidIn(this.level);
                    return !this.invalidTentativeBurst;
                }
            }

            return false;
        }
    }

    @Override
    public void ibs$setReceiver(ManaReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void ibs$setLastTentativeBurst(List<ManaBurstEntity.PositionProperties> lastTentativeBurst) {
        this.lastTentativeBurst = lastTentativeBurst;
    }
}
