package xyz.imcodist.data;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.imcodist.QuickMenu;
import xyz.imcodist.data.command_actions.BaseActionData;
import xyz.imcodist.data.command_actions.CommandActionData;
import xyz.imcodist.data.command_actions.KeybindActionData;
import xyz.imcodist.other.KeybindHandler;
import xyz.imcodist.other.ModConfigModel;

import java.util.ArrayList;

public class ActionButtonData {
    public String name;
    public ArrayList<BaseActionData> actions = new ArrayList<>();
    public ItemStack icon;
    public ArrayList<Integer> keybind = new ArrayList<>();

    public boolean keyPressed = false;

    public ActionButtonDataJSON toJSON() {
        ActionButtonDataJSON jsonData = new ActionButtonDataJSON();

        jsonData.name = name;
        jsonData.actions = new ArrayList<>();

        jsonData.keybind = keybind;

        actions.forEach((action) -> {
            ArrayList<String> actionArray = new ArrayList<>();
            actionArray.add(action.getJsonType());
            actionArray.add(action.getJsonValue());

            jsonData.actions.add(actionArray);
        });

        if (icon != null) jsonData.icon = icon.getRegistryEntry().value().toString();

        return jsonData;
    }

    public static ActionButtonData fromJSON(ActionButtonDataJSON json) {
        ActionButtonData data = new ActionButtonData();

        data.name = json.name;
        data.actions = new ArrayList<>();

        data.keybind = json.keybind;

        json.actions.forEach((actionArray) -> {
            BaseActionData actionData = getActionDataType(actionArray.get(0), actionArray.get(1));
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

    public InputUtil.Key getKey() {
        if (keybind.size() < 4) return null;
        return InputUtil.fromKeyCode(keybind.get(0), keybind.get(1));
    }

    public void run() {
        run(false);
    }

    public void run(boolean isKeybind) {
        // Show run message.
        ModConfigModel.DisplayRunText displayRunText = QuickMenu.CONFIG.displayRunText();
        if (displayRunText == ModConfigModel.DisplayRunText.ALWAYS || displayRunText == ModConfigModel.DisplayRunText.KEYBIND_ONLY && isKeybind) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && client.player != null) {
                client.player.sendMessage(Text.of("Ran action \"" + name + "\""), true);
            }
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        // Run the buttons action.
        actions.forEach((action) -> {
            if (action instanceof CommandActionData commandAction) {
                // Run the command.
                String commandToRun = commandAction.command;

                if (commandToRun != null) {
                    if (commandToRun.startsWith("/")) {
                        commandToRun = commandToRun.substring(1);
                        player.networkHandler.sendChatCommand(commandToRun);
                    } else {
                        player.networkHandler.sendChatMessage(commandToRun);
                    }
                }
            }

            if (action instanceof KeybindActionData keybindAction) {
                KeybindHandler.pressKey(keybindAction.keybindTranslationKey);
            }
        });
    }
}

