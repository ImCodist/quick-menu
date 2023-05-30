package xyz.imcodist.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ActionData {
    public String name;
    public String action;
    public ItemStack icon;

    public ActionDataJSON toJSON() {
        ActionDataJSON jsonData = new ActionDataJSON();

        jsonData.name = name;
        jsonData.action = action;

        if (icon != null) jsonData.icon = icon.getRegistryEntry().value().toString();

        return jsonData;
    }

    public static ActionData fromJSON(ActionDataJSON json) {
        ActionData data = new ActionData();

        data.name = json.name;
        data.action = json.action;

        if (json.icon != null) data.icon = new ItemStack(Registries.ITEM.get(new Identifier(json.icon)));

        return data;
    }
}
