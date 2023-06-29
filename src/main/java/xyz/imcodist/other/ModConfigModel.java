package xyz.imcodist.other;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;

@Modmenu(modId = "quickmenu")
@Config(name = "quickmenu", wrapperName = "ModConfig")
public class ModConfigModel {
    public enum DisplayRunText {
        ALWAYS, KEYBIND_ONLY, NEVER
    }

    @RangeConstraint(min = 60, max = 150*3)
    public int menuWidth = 180;
    @RangeConstraint(min = 60, max = 150*3)
    public int menuHeight = 114;

    public DisplayRunText displayRunText = DisplayRunText.KEYBIND_ONLY;

    public boolean closeOnAction = true;
    public boolean closeOnKeyReleased = false;
    public boolean showActionsInTooltip = true;
}
