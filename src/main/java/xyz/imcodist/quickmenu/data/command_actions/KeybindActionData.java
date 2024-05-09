package xyz.imcodist.quickmenu.data.command_actions;

import net.minecraft.text.Text;
import xyz.imcodist.quickmenu.other.KeybindHandler;

public class KeybindActionData extends BaseActionData {
    public String keybindTranslationKey = "";

    @Override
    public String getJsonType() {
        return "key";
    }
    @Override
    public String getJsonValue() {
        return keybindTranslationKey;
    }

    @Override
    public String getTypeString() { return "KEY"; }
    @Override
    public String getString() {
        return Text.translatable(keybindTranslationKey).getString();
    }

    @Override
    public void run() {
        KeybindHandler.pressKey(keybindTranslationKey);
    }
}
