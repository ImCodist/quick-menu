package xyz.imcodist.data;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ButtonDataHandler {
    public static List<ButtonData> buttons = new ArrayList<>();

    public static void initialize() {
        ButtonData dataCreative = new ButtonData();
        dataCreative.name = "Creative Mode";
        dataCreative.action = "/gamemode creative";
        dataCreative.icon = new ItemStack(Items.GRASS_BLOCK);

        buttons.add(dataCreative);

        ButtonData dataHome = new ButtonData();
        dataHome.name = "Home";
        dataHome.action = "/home bed";
        dataHome.icon = new ItemStack(Items.RED_BED);

        buttons.add(dataHome);
    }
}
