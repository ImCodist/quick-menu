package xyz.imcodist.data.command_actions;

import net.minecraft.text.Text;

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
    public String getString() {
        return Text.translatable(keybindTranslationKey).getString();
    }
}
