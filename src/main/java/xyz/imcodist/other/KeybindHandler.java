package xyz.imcodist.other;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import xyz.imcodist.mixins.KeyBindingMixin;

import java.util.ArrayList;

public class KeybindHandler {
    public static ArrayList<KeyBinding> queuedKeys = new ArrayList<>();
    private static final ArrayList<KeyBinding> queuedRelease = new ArrayList<>();

    private static boolean didPress = false;

    public static void runQueue() {
        if (didPress) {
            for (KeyBinding keyBinding : queuedRelease) {
                keyBinding.setPressed(false);
            }

            didPress = false;
            queuedRelease.clear();
        }

        for (KeyBinding keyBinding : queuedKeys) {
            KeyBindingMixin keyBindingMixin = (KeyBindingMixin) keyBinding;

            keyBindingMixin.setTimesPressed(1);
            keyBinding.setPressed(true);

            didPress = true;
            queuedRelease.add(keyBinding);
        }

        queuedKeys.clear();
    }

    public static void pressKey(String translationKey) {
        KeyBinding keyBinding = getFromTranslationKey(translationKey);
        if (keyBinding == null) return;

        queuedKeys.add(keyBinding);
    }

    public static KeyBinding getFromTranslationKey(String translationKey) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            for (KeyBinding keyBinding : client.options.allKeys) {
                if (keyBinding.getTranslationKey().equals(translationKey)) return keyBinding;
            }
        }

        return null;
    }

    public static KeyBinding[] getKeybindings() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;

        return client.options.allKeys;
    }
}
