package dev.beecube31.improved_botania_spreaders.datagen;

import dev.beecube31.improved_botania_spreaders.core.IBSCore;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = IBSCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IBSDatagen {

    public IBSDatagen() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        IBSBlockTagProvider prov = new IBSBlockTagProvider(output, lookupProvider, existingFileHelper);

        if (event.includeServer()) {
            generator.addProvider(true, new IBSRecipeProvder(output));
            generator.addProvider(true, prov);
        }
    }
}
