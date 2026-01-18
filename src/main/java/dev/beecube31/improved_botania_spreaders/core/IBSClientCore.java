package dev.beecube31.improved_botania_spreaders.core;

import com.google.common.base.Suppliers;
import dev.beecube31.improved_botania_spreaders.render.Models;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vazkii.botania.api.BotaniaForgeClientCapabilities;
import vazkii.botania.api.block.WandHUD;
import vazkii.botania.forge.CapabilityUtil;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

@Mod.EventBusSubscriber(modid = IBSCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class IBSClientCore {
    private static final Supplier<Map<BlockEntityType<?>, Function<BlockEntity, WandHUD>>> WAND_HUD = Suppliers.memoize(() -> {
        var ret = new IdentityHashMap<BlockEntityType<?>, Function<BlockEntity, WandHUD>>();

        IBSBlockTiles.registerWandHud((factory, types) -> {
            for (var type : types) {
                ret.put(type, factory);
            }
        });

        return Collections.unmodifiableMap(ret);
    });

    @SubscribeEvent
    public static void onClientInitEv(FMLClientSetupEvent evt) {
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, IBSClientCore::attachBeCapabilities);
    }

    @SubscribeEvent
    public static void onModelRegisterEv(ModelEvent.RegisterAdditional evt) {
        var resourceManager = Minecraft.getInstance().getResourceManager();
        Models.INSTANCE.onModelRegister(resourceManager, evt::register);
    }

    @SubscribeEvent
    public static void onModelBakeEv(ModelEvent.BakingCompleted evt) {
        Models.INSTANCE.onModelBake(evt.getModels());
    }

    private static void attachBeCapabilities(AttachCapabilitiesEvent<BlockEntity> e) {
        var be = e.getObject();

        var makeWandHud = WAND_HUD.get().get(be.getType());
        if (makeWandHud != null) {
            e.addCapability(prefix("wand_hud"),
                    CapabilityUtil.makeProvider(BotaniaForgeClientCapabilities.WAND_HUD, makeWandHud.apply(be)));
        }
    }
}
