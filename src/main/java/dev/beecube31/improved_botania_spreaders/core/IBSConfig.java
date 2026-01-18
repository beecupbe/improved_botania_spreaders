package dev.beecube31.improved_botania_spreaders.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = IBSCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IBSConfig {
    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig {
        public final ForgeConfigSpec.IntValue nilfheimSpreaderMaxMana;
        public final ForgeConfigSpec.IntValue muspelheimSpreaderMaxMana;
        public final ForgeConfigSpec.IntValue alfheimSpreaderMaxMana;
        public final ForgeConfigSpec.IntValue asgardSpreaderMaxMana;

        public final ForgeConfigSpec.IntValue nilfheimSpreaderManaPerBurst;
        public final ForgeConfigSpec.IntValue muspelheimSpreaderManaPerBurst;
        public final ForgeConfigSpec.IntValue alfheimSpreaderManaPerBurst;
        public final ForgeConfigSpec.IntValue asgardSpreaderManaPerBurst;


        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Mana Spreaders configuration").push("spreaders");

            nilfheimSpreaderMaxMana = builder
                    .defineInRange("nilfheimSpreaderMaxMana", 1000 * 10, 0, Integer.MAX_VALUE);

            muspelheimSpreaderMaxMana = builder
                    .defineInRange("muspelheimSpreaderMaxMana", 1000 * 100, 0, Integer.MAX_VALUE);

            alfheimSpreaderMaxMana = builder
                    .defineInRange("alfheimSpreaderMaxMana", 1000 * 1000, 0, Integer.MAX_VALUE);

            asgardSpreaderMaxMana = builder
                    .defineInRange("asgardSpreaderMaxMana", 1000 * 2000, 0, Integer.MAX_VALUE);


            nilfheimSpreaderManaPerBurst = builder
                    .defineInRange("nilfheimSpreaderManaPerBurst", 1000 * 10, 0, Integer.MAX_VALUE);

            muspelheimSpreaderManaPerBurst = builder
                    .defineInRange("muspelheimSpreaderManaPerBurst", 1000 * 100, 0, Integer.MAX_VALUE);

            alfheimSpreaderManaPerBurst = builder
                    .defineInRange("alfheimSpreaderManaPerBurst", 1000 * 1000, 0, Integer.MAX_VALUE);

            asgardSpreaderManaPerBurst = builder
                    .defineInRange("asgardSpreaderManaPerBurst", 1000 * 2000, 0, Integer.MAX_VALUE);


            builder.pop();
        }
    }

    public static void register(net.minecraftforge.fml.ModLoadingContext context) {
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, COMMON_SPEC, "improved_botania_spreaders-common.toml");
    }
}
