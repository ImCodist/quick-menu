package xyz.imcodist.other;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.SectionHeader;

@Modmenu(modId = "quickmenu")
@Config(name = "quickmenu", wrapperName = "ModConfig")
public class ModConfigModel {
    public enum DisplayRunText {
        ALWAYS, KEYBIND_ONLY, NEVER
    }

    @SectionHeader("menu")
    @RangeConstraint(min = 60, max = 150*3)
    public int menuWidth = 180;
    @RangeConstraint(min = 60, max = 150*3)
    public int menuHeight = 114;

    @RangeConstraint(min = 1, max = 14)
    public int buttonsPerRow = 5;

    public boolean closeOnKeyReleased = false;

    public boolean hideEditIcon = false;

    @SectionHeader("action_buttons")
    public boolean closeOnAction = true;
    public boolean showActionsInTooltip = true;
    public DisplayRunText displayRunText = DisplayRunText.KEYBIND_ONLY;
}
