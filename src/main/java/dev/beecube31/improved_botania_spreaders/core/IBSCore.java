package dev.beecube31.improved_botania_spreaders.core;

import dev.beecube31.improved_botania_spreaders.tiles.TileImprovedSpreader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.BotaniaRegistries;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.common.item.CustomCreativeTabContents;
import vazkii.botania.forge.CapabilityUtil;

import java.util.*;
import java.util.function.*;

import static dev.beecube31.improved_botania_spreaders.core.IBSElements.prefix;

@Mod(IBSCore.MODID)
@Mod.EventBusSubscriber(modid = IBSCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IBSCore {
    public static final String MODID = "improved_botania_spreaders";

    private final Set<Item> itemsToAddToCreativeTab = new LinkedHashSet<>();

    public IBSCore() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::addCreative);

        IBSConfig.register(net.minecraftforge.fml.ModLoadingContext.get());

        bind(Registries.BLOCK, IBSElements::registerOwnBlocks);
        bindForItems(IBSElements::registerOwnItemBlocks);
        bind(Registries.BLOCK_ENTITY_TYPE, IBSBlockTiles::registerOwnTiles);

        bus.addListener((BuildCreativeModeTabContentsEvent e) -> {
            if (e.getTabKey() == BotaniaRegistries.BOTANIA_TAB_KEY) {
                for (Item item : this.itemsToAddToCreativeTab) {
                    if (item instanceof CustomCreativeTabContents cc) {
                        cc.addToCreativeTab(item, e);
                    } else if (item instanceof BlockItem bi && bi.getBlock() instanceof CustomCreativeTabContents cc) {
                        cc.addToCreativeTab(item, e);
                    } else {
                        e.accept(item);
                    }
                }
            }
        });

        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, IBSCore::attachBeCapabilities);
    }

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(MODID, path);
    }

    private static void attachBeCapabilities(AttachCapabilitiesEvent<BlockEntity> e) {
        var be = e.getObject();
        if (e.getObject() instanceof TileImprovedSpreader) {
            e.addCapability(prefix("mana_receiver"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.MANA_RECEIVER, (ManaReceiver) be));
            e.addCapability(prefix("wandable"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.WANDABLE, (Wandable) be));
        }
    }

    private void bindForItems(Consumer<BiConsumer<Item, ResourceLocation>> source) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registries.ITEM)) {
                source.accept((t, rl) -> {
                    itemsToAddToCreativeTab.add(t);
                    event.register(Registries.ITEM, rl, () -> t);
                });
            }
        });
    }

    private static <T> void bind(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) -> {
            if (registry.equals(event.getRegistryKey())) {
                source.accept((t, rl) -> event.register(registry, rl, () -> t));
            }
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == BotaniaRegistries.BOTANIA_TAB_KEY) {
            event.accept(IBSElements.NILFHEIM_SPREADER);
            event.accept(IBSElements.MUSPELHEIM_SPREADER);
            event.accept(IBSElements.ALFHEIM_SPREADER);
            event.accept(IBSElements.ASGARD_SPREADER);
        }
    }
}