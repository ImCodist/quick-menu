package xyz.imcodist.data;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import xyz.imcodist.data.command_actions.BaseActionData;
import xyz.imcodist.data.command_actions.CommandActionData;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionData {
    public String name;
    public ArrayList<BaseActionData> actions = new ArrayList<>();
    public ItemStack icon;

    public ActionDataJSON toJSON() {
        ActionDataJSON jsonData = new ActionDataJSON();

        jsonData.name = name;
        jsonData.actions = new HashMap<>();

        actions.forEach((action) -> jsonData.actions.put(action.getJsonType(), action.getJsonValue()));

        if (icon != null) jsonData.icon = icon.getRegistryEntry().value().toString();

        return jsonData;
    }

    public static ActionData fromJSON(ActionDataJSON json) {
        ActionData data = new ActionData();

        data.name = json.name;
        data.actions = new ArrayList<>();

        json.actions.forEach((k, v) -> {
            BaseActionData actionData = getActionDataType(k, v);
            data.actions.add(actionData);
        });

        if (json.icon != null) data.icon = new ItemStack(Registries.ITEM.get(new Identifier(json.icon)));

        return data;
    }

    private static BaseActionData getActionDataType(String type, String value) {
        switch (type) {
            case "base" -> {
                return new BaseActionData();
            }
            case "cmd" -> {
                CommandActionData commandActionData = new CommandActionData();
                commandActionData.command = value;
                return commandActionData;
            }
        }

        return null;
    }
}

