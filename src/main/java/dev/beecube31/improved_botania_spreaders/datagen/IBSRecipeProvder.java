package dev.beecube31.improved_botania_spreaders.datagen;


import dev.beecube31.improved_botania_spreaders.core.IBSElements;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.item.BotaniaItems;

import java.util.function.Consumer;

public class IBSRecipeProvder extends RecipeProvider {

    public IBSRecipeProvder(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.TOOLS, IBSElements.NILFHEIM_SPREADER)
                .define('P', BotaniaItems.runePride)
                .define('T', BotaniaItems.terrasteel)
                .define('S', BotaniaBlocks.shimmerrock)
                .define('F', BotaniaBlocks.fabulousPool)
                .pattern("   ")
                .pattern("PFP")
                .pattern("STS")
                .unlockedBy("has_string", has(BotaniaBlocks.fabulousPool))
                .save(consumer);

        ShapedRecipeBuilder
                .shaped(RecipeCategory.TOOLS, IBSElements.MUSPELHEIM_SPREADER)
                .define('P', BotaniaItems.runeSpring)
                .define('S', BotaniaBlocks.elementiumBlock)
                .define('F', IBSElements.NILFHEIM_SPREADER)
                .define('G', BotaniaItems.lifeEssence)
                .pattern("   ")
                .pattern("PFP")
                .pattern("SGS")
                .unlockedBy("has_string", has(IBSElements.NILFHEIM_SPREADER))
                .save(consumer);

        ShapedRecipeBuilder
                .shaped(RecipeCategory.TOOLS, IBSElements.ALFHEIM_SPREADER)
                .define('P', BotaniaItems.runeGluttony)
                .define('S', BotaniaBlocks.elementiumBlock)
                .define('F', IBSElements.MUSPELHEIM_SPREADER)
                .define('G', BotaniaItems.gaiaIngot)
                .pattern("   ")
                .pattern("PFP")
                .pattern("SGS")
                .unlockedBy("has_string", has(IBSElements.MUSPELHEIM_SPREADER))
                .save(consumer);

        ShapedRecipeBuilder
                .shaped(RecipeCategory.TOOLS, IBSElements.ASGARD_SPREADER)
                .define('P', BotaniaItems.runeEnvy)
                .define('S', BotaniaBlocks.terrasteelBlock)
                .define('F', IBSElements.ALFHEIM_SPREADER)
                .define('G', BotaniaItems.gaiaIngot)
                .pattern("   ")
                .pattern("PFP")
                .pattern("GSG")
                .unlockedBy("has_string", has(IBSElements.ALFHEIM_SPREADER))
                .save(consumer);

    }
}
