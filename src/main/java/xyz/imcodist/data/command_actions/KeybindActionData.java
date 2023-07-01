package xyz.imcodist.data.command_actions;

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
        return keybindTranslationKey;
    }
}
