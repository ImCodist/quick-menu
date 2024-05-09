package xyz.imcodist.quickmenu.mixins;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface KeyBindingMixin {
    @Accessor("timesPressed")
    void setTimesPressed(int timesPressed);
}
