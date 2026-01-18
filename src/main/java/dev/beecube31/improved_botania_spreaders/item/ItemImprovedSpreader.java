package dev.beecube31.improved_botania_spreaders.item;

import dev.beecube31.improved_botania_spreaders.block.BlockImprovedSpreader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemImprovedSpreader extends BlockItem {
    private final BlockImprovedSpreader.Variant variant;

    public ItemImprovedSpreader(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);

        this.variant = ((BlockImprovedSpreader) p_40565_).variant;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flags) {
        tooltip.add(Component.translatable("ibs.spreader_capacity.formatted", this.variant.manaCapacity.get())
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));

        tooltip.add(Component.translatable("ibs.spreader_mana_per_burst.formatted", this.variant.burstMana.get())
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }
}
