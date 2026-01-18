package dev.beecube31.improved_botania_spreaders.datagen;

import dev.beecube31.improved_botania_spreaders.core.IBSElements;
import dev.beecube31.improved_botania_spreaders.core.IBSCore;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.lib.ResourceLocationHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class IBSBlockTagProvider extends BlockTagsProvider {
    private static final TagKey<Block> MANA_SPREADERS = tag("mana_spreaders");

    public IBSBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, IBSCore.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        generateDefaultTags(IBSElements.ALFHEIM_SPREADER);
        generateDefaultTags(IBSElements.MUSPELHEIM_SPREADER);
        generateDefaultTags(IBSElements.ASGARD_SPREADER);
        generateDefaultTags(IBSElements.NILFHEIM_SPREADER);
    }

    private void generateDefaultTags(Block block) {
        this.tag(MANA_SPREADERS).add(block);
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(block);
    }

    private static TagKey<Block> tag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocationHelper.prefix(name));
    }
}

