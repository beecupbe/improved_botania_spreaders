package dev.beecube31.improved_botania_spreaders.interfaces;

import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.common.entity.ManaBurstEntity;

import java.util.List;

public interface IMixinSpreader {
    void ibs$setNoRequestsClientUpdate();

    boolean ibs$needsNewBurstSimulation();

    void ibs$setPoweredLastTick(boolean powered);

    void ibs$setRequestsClientUpdate(boolean req);

    void ibs$setReceiverLastTick(ManaReceiver receiver);

    void ibs$setReceiver(ManaReceiver receiver);

    void ibs$setLastTentativeBurst(List<ManaBurstEntity.PositionProperties> lastTentativeBurst);
}
