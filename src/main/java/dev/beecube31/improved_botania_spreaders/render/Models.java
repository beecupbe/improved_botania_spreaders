package dev.beecube31.improved_botania_spreaders.render;

import dev.beecube31.improved_botania_spreaders.core.IBSCore;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.lib.ResourceLocationHelper;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Models {
    private static final ResourceLocation nilfheimSpreaderCoreId = IBSCore.prefix("block/nilfheim_spreader_core");
    private static final ResourceLocation muspelheimSpreaderCoreId = IBSCore.prefix("block/muspelheim_spreader_core");
    private static final ResourceLocation alfheimSpreaderCoreId = IBSCore.prefix("block/alfheim_spreader_core");
    private static final ResourceLocation asgardSpreaderCoreId = IBSCore.prefix("block/asgard_spreader_core");

    private static final ResourceLocation nilfheimSpreaderScaffoldingId = IBSCore.prefix("block/nilfheim_spreader_scaffolding");
    private static final ResourceLocation muspelheimSpreaderScaffoldingId = IBSCore.prefix("block/muspelheim_spreader_scaffolding");
    private static final ResourceLocation alfheimSpreaderScaffoldingId = IBSCore.prefix("block/alfheim_spreader_scaffolding");
    private static final ResourceLocation asgardSpreaderScaffoldingId = IBSCore.prefix("block/asgard_spreader_scaffolding");

    private static final Map<DyeColor, ResourceLocation> spreaderPaddingIds = new EnumMap<>(ColorHelper.supportedColors().collect(Collectors.toMap(
            Function.identity(), (color) -> IBSCore.prefix("block/" + color.getSerializedName() + "_spreader_padding"))));
    public static final Models INSTANCE = new Models();
    private final Map<ResourceLocation, Consumer<BakedModel>> modelConsumers;
    public boolean registeredModels = false;

    public BakedModel nilfheimSpreaderCore;
    public BakedModel muspelheimSpreaderCore;
    public BakedModel alfheimSpreaderCore;
    public BakedModel asgardSpreaderCore;

    public BakedModel nilfheimSpreaderScaffolding;
    public BakedModel muspelheimSpreaderScaffolding;
    public BakedModel alfheimSpreaderScaffolding;
    public BakedModel asgardSpreaderScaffolding;

    public final HashMap<DyeColor, BakedModel> spreaderPaddings = new HashMap<>();

    public void onModelRegister(ResourceManager rm, Consumer<ResourceLocation> consumer) {
        this.modelConsumers.keySet().forEach(consumer);
        if (!this.registeredModels) {
            this.registeredModels = true;
        }

    }

    public void onModelBake(Map<ResourceLocation, BakedModel> modelRegistry) {
        for (Map.Entry<ResourceLocation, Consumer<BakedModel>> entry : this.modelConsumers.entrySet()) {
            ResourceLocation id = entry.getKey();
            BakedModel model = modelRegistry.get(id);
            if (model != null) {
                entry.getValue().accept(model);
            }
        }
    }

    private Models() {
        modelConsumers = new HashMap<>();
        modelConsumers.put(nilfheimSpreaderCoreId, bakedModel -> this.nilfheimSpreaderCore = bakedModel);
        modelConsumers.put(muspelheimSpreaderCoreId, bakedModel -> this.muspelheimSpreaderCore = bakedModel);
        modelConsumers.put(alfheimSpreaderCoreId, bakedModel -> this.alfheimSpreaderCore = bakedModel);
        modelConsumers.put(asgardSpreaderCoreId, bakedModel -> this.asgardSpreaderCore = bakedModel);

        modelConsumers.put(nilfheimSpreaderScaffoldingId, bakedModel -> this.nilfheimSpreaderScaffolding = bakedModel);
        modelConsumers.put(muspelheimSpreaderScaffoldingId, bakedModel -> this.muspelheimSpreaderScaffolding = bakedModel);
        modelConsumers.put(alfheimSpreaderScaffoldingId, bakedModel -> this.alfheimSpreaderScaffolding = bakedModel);
        modelConsumers.put(asgardSpreaderScaffoldingId, bakedModel -> this.asgardSpreaderScaffolding = bakedModel);
        for (var color : spreaderPaddingIds.keySet()) {
            modelConsumers.put(spreaderPaddingIds.get(color), bakedModel -> spreaderPaddings.put(color, bakedModel));
        }
    }
}
