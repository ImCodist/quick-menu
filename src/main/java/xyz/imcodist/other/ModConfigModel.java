package xyz.imcodist.other;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;

import java.util.ArrayList;
import java.util.List;

@Modmenu(modId = "quickmenu")
@Config(name = "quickmenu", wrapperName = "ModConfig")
public class ModConfigModel {
    @RangeConstraint(min = 60, max = 150*3)
    public int menuWidth = 180;
    @RangeConstraint(min = 60, max = 150*3)
    public int menuHeight = 114;
}
