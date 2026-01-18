package dev.beecube31.improved_botania_spreaders.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.common.block.block_entity.mana.ManaSpreaderBlockEntity;

@Mixin(ManaSpreaderBlockEntity.class)
public interface AccessorTileManaSpreader {
    @Accessor("mana") int mana();

    @Accessor("requestsClientUpdate") boolean requestsClientUpdate();

    @Accessor("inputKey") String inputKey();

    @Accessor("receiver") ManaReceiver receiver();

    @Accessor("mapmakerOverride") boolean mapmakerOverride();

    @Accessor("poweredLastTick") boolean poweredLastTick();

    @Accessor("invalidTentativeBurst") boolean invalidTentativeBurst();

    @Accessor("receiverLastTick") ManaReceiver receiverLastTick();

    @Accessor("mmForcedColor") int mmForcedColor();

    @Accessor("mmForcedManaPayload") int mmForcedManaPayload();

    @Accessor("mmForcedTicksBeforeManaLoss") int mmForcedTicksBeforeManaLoss();

    @Accessor("mmForcedManaLossPerTick") float mmForcedManaLossPerTick();

    @Accessor("mmForcedGravity") float mmForcedGravity();

    @Accessor("mmForcedVelocityMultiplier") float mmForcedVelocityMultiplier();
}
